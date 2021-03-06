package com.infa.products.discovery.automatedclassification.dataset;

import com.infa.products.discovery.automatedclassification.engine.BusinessTermSearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.IndexBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngine;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.IndexWriterException;
import com.infa.products.discovery.automatedclassification.exception.InvalidAlignmentCandidatesException;
import com.infa.products.discovery.automatedclassification.model.BusinessTerm;
import com.infa.products.discovery.automatedclassification.model.BusinessTermType;
import com.infa.products.discovery.automatedclassification.model.Synonym;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;
import com.infa.products.discovery.automatedclassification.util.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

public class AnaCreditBusinessTermSearchTest {

    @BeforeClass
    public void setUp() {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);
    }

    @Test(testName = "testAnaCreditDataset", enabled = true, groups = {"TermSearchDatasetTests"}, description = "Test term search with AnaCredit dataset")
    public void testAnaCreditDataset()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
//        final Long expectedCorrectMatch = 53L;
//        final Long expectedIncorrectMatch = 1L;
//        final Long expectedNoMatch = 41L;
        ExecutorServiceUtil searchExectorServiceUtil = ExecutorServiceUtil.newInstance(8);
        AnaCreditDatasetReader reader = new AnaCreditDatasetReader();
        reader.read();

        List<BusinessTerm> businessTerms = new LinkedList<>();
        int index = 1;
        List<String> glossaryTerms = reader.getBussinessGlossaryTerms();
        for (String term : glossaryTerms) {
            businessTerms.add(Utils.createBusinessTerm(String.format("BT%x", index++), term, "BG"));
        }

        List<Synonym> synonyms = new ArrayList<>();

        //todo debugging purpose only
        synonyms.add(new Synonym("accntng", "accounting"));
        synonyms.add(new Synonym("clssfctn", "classification"));
        synonyms.add(new Synonym("typ", "type"));
        synonyms.add(new Synonym("entrprs", "enterprise"));
        synonyms.add(new Synonym("sz", "size"));
        businessTerms.add(Utils.createBusinessTerm(String.format("BT%x", index++), "enterprise siz", "BG"));

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        engineBuilder.withSynonyms(synonyms);
        engineBuilder.withStopWords(ENGLISH_STOP_WORD_SET);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        Map<String, String> assetsMap = reader.getColumnNames();
        AtomicLong correctMatch = new AtomicLong();
        AtomicLong incorrectMatch = new AtomicLong();
        AtomicLong noMatch = new AtomicLong();
        AtomicLong correctRecommendation = new AtomicLong();
        AtomicLong incorrectRecommendation = new AtomicLong();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            for (String assetId : assetsMap.keySet()) {
                String asset = assetsMap.get(assetId);
                String correctTerm = reader.getAssociatedBusinessTerm(assetId);
                searchExectorServiceUtil.submit(new SearchTask(asset, searcher, correctTerm, correctMatch, incorrectMatch, correctRecommendation, incorrectRecommendation, noMatch));
            }
            searchExectorServiceUtil.close();
            engine.close();
            System.out.println("Correct Match: " + correctMatch);
            System.out.println("Incorrect Match: " + incorrectMatch);
            System.out.println("Correct Recommendations: " + correctRecommendation);
            System.out.println("Incorrect Recommendations: " + incorrectRecommendation);
            System.out.println("No Match: " + noMatch);
            System.out.println("Anacredittermsearchtest.class");

//            Assert.assertEquals(java.util.Optional.of(correctMatch.get()), expectedCorrectMatch);
//            Assert.assertEquals(java.util.Optional.of(incorrectMatch.get()), expectedIncorrectMatch);
//            Assert.assertEquals(java.util.Optional.of(noMatch.get()), expectedNoMatch);
        }

    }
}
