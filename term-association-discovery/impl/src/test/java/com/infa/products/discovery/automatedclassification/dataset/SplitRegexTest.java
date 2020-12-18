package com.infa.products.discovery.automatedclassification.dataset;

import com.infa.products.discovery.automatedclassification.engine.BusinessTermSearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.IndexBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngine;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.IndexWriterException;
import com.infa.products.discovery.automatedclassification.model.BusinessTerm;
import com.infa.products.discovery.automatedclassification.model.BusinessTermType;
import com.infa.products.discovery.automatedclassification.model.Synonym;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;
import com.infa.products.discovery.automatedclassification.util.ConfigUtil;
import com.infa.products.discovery.automatedclassification.util.ExecutorServiceUtil;
import com.infa.products.discovery.automatedclassification.util.SearchTask;
import com.infa.products.discovery.automatedclassification.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

public class SplitRegexTest {

    @BeforeClass
    public void setUp() {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);
    }

    private final String ANTHEM_COLS_SPLIT_REGEX_CSV_FILE_PATH = "src/test/resources/sampledata/EDWard_Columns_Split_Regex_Test.csv";

    @Test(testName = "testAnthemDatasetSplitRegex", enabled = true, groups = {
            "TermSearchDatasetTests"}, description = "Test term search with Anthem dataset for SplitRegex functionality")
    public void testAnthemDatasetForSplitRegex() throws IndexWriterException, IOException, DocumentSearchException {
        final Long expectedCorrectMatch = 465L;
        final Long expectedIncorrectMatch = 1L;
        final Long expectedNoMatch = 38L;
        ExecutorServiceUtil searchExecutorServiceUtil = ExecutorServiceUtil.newInstance(8);
        AnthemColsDatasetReader reader = new AnthemColsDatasetReader(ANTHEM_COLS_SPLIT_REGEX_CSV_FILE_PATH);
        reader.read();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        int index = 1;
        List<String> glossaryTerms = reader.getBussinessGlossaryTerms();
        for (String term : glossaryTerms) {
            businessTerms.add(Utils.createBusinessTerm(String.format("BT%x", index++), term, "BG"));
        }

        List<Synonym> synonyms = new ArrayList<>();
        Map<String, List<String>> abbrs = AnthemAbbreviationReader.getAbbreviations();
        for (String abbr : abbrs.keySet()) {
            for (String longForm : abbrs.get(abbr)) {
                String sf = abbr.toLowerCase().trim();
                String lf = longForm.toLowerCase().trim();
                synonyms.add(new Synonym(sf, lf));
            }
        }
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
                searchExecutorServiceUtil.submit(new SearchTask(asset, searcher, correctTerm, correctMatch, incorrectMatch, correctRecommendation, incorrectRecommendation, noMatch));
            }
            searchExecutorServiceUtil.close();
            engine.close();
        }
        System.out.println("total asserts: " + assetsMap.keySet().size());
        System.out.println("Correct Match: " + correctMatch);
        System.out.println("Incorrect Match: " + incorrectMatch);
        System.out.println("Correct Recommendations: " + correctRecommendation);
        System.out.println("Incorrect Recommendations: " + incorrectRecommendation);
        System.out.println("No Match: " + noMatch);
//        assert correctMatch.get() == expectedCorrectMatch;
//        assert incorrectMatch.get() == expectedIncorrectMatch;
//        assert noMatch.get() == expectedNoMatch;
    }
}
