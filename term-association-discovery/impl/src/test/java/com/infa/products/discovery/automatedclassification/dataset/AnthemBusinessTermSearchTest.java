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
import com.infa.products.discovery.automatedclassification.util.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

public class AnthemBusinessTermSearchTest {

    private final String ANTHEM_COLS_CSV_FILE_PATH = "src/test/resources/sampledata/EDWard_Columns.csv";

    @BeforeClass
    public void setUp() {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);
    }

    private void testAnthemDataset(int numOfSearchThreads)
            throws IndexWriterException, IOException, DocumentSearchException {
        final Long expectedCorrectMatch = 6616L;
        final Long expectedIncorrectMatch = 67L;
        final Long expectedNoMatch = 986L;
        numOfSearchThreads = numOfSearchThreads <= 0 ? 1 : numOfSearchThreads;
        ExecutorServiceUtil searchExecutorServiceUtil = ExecutorServiceUtil.newInstance(numOfSearchThreads);
        AnthemColsDatasetReader reader = new AnthemColsDatasetReader(ANTHEM_COLS_CSV_FILE_PATH);
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
                if (asset.equalsIgnoreCase("TOTL_DRUG_SPEND_APLYD_AMT")) {
                    System.out.print("");
                }
                String correctTerm = reader.getAssociatedBusinessTerm(assetId);
                searchExecutorServiceUtil.submit(new SearchTask(asset, searcher, correctTerm, correctMatch, incorrectMatch, correctRecommendation, incorrectRecommendation, noMatch));
            }
            searchExecutorServiceUtil.close();
            engine.close();
        }
        System.out.println("Correct Match: " + correctMatch);
        System.out.println("Incorrect Match: " + incorrectMatch);
        System.out.println("Correct Recommendations: " + correctRecommendation);
        System.out.println("Incorrect Recommendations: " + incorrectRecommendation);
        System.out.println("No Match: " + noMatch);
        Assert.assertEquals(correctMatch.toString(), expectedCorrectMatch.toString());
        Assert.assertEquals(incorrectMatch.toString(), expectedIncorrectMatch.toString());
        Assert.assertEquals(noMatch.toString(), expectedNoMatch.toString());
    }

    @Test(testName = "testAnthemDatasetParallelSearch", enabled = true, groups = {
            "TermSearchDatasetTests"}, description = "Test term search with Anthem dataset using Parallel Search")
    public void multiThreadSearchTest() throws IndexWriterException, IOException, DocumentSearchException {
        long singleThreadedSearchStartTime = System.currentTimeMillis();
        //testAnthemDataset(1);
        long singleThreadedSearchEndTime = System.currentTimeMillis();
        long multiThreadedSearchStartTime = System.currentTimeMillis();
        testAnthemDataset(8);
        long multiThreadedSearchEndTime = System.currentTimeMillis();
        System.out.println("Time difference of search operation between 1 Thread and 8 Threads is : " + ((singleThreadedSearchEndTime - singleThreadedSearchStartTime) - (multiThreadedSearchEndTime - multiThreadedSearchStartTime)) + " in ms.");
    }
}
