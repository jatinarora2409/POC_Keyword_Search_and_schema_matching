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
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;
import com.infa.products.discovery.automatedclassification.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

public class RealEstateBusinessGlossaryMatchTest {

    int dict_values = 0;
    @BeforeClass
    public void setUp() {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);
    }

    private Set<String> terms = new HashSet<>();

    private Map<String, List<String>> colIdBtMap = new HashMap<>();

    private Map<String, String> columnIdNameMap = new HashMap<>();

    private Set<String> colsWithoutBt = new HashSet<>();

    private Set<String> unique = new HashSet<>();

    @Test(testName = "RealEstateTest", enabled = true, groups = {"TermSearchDatasetTests"}, description = "Test term search with Airlines dataset")
    public void testAirlinesDataset()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {

        ExecutorServiceUtil searchExectorServiceUtil = ExecutorServiceUtil.newInstance(2);

        read("redfin");

        List<BusinessTerm> businessTerms = new LinkedList<>();
        int index = 1;
        List<String> glossaryTerms = getBussinessGlossaryTerms();
        for (String term : glossaryTerms) {
            businessTerms.add(Utils.createBusinessTerm(String.format("BT%x", index++), term, "BG"));
        }

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        engineBuilder.withStopWords(ENGLISH_STOP_WORD_SET);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        Map<String, String> assetsMap = getColumnNames();
        AtomicLong correctMatch = new AtomicLong();
        AtomicLong incorrectMatch = new AtomicLong();
        AtomicLong noMatch = new AtomicLong();
        AtomicLong correctRecommendation = new AtomicLong();
        AtomicLong incorrectRecommendation = new AtomicLong();
        System.out.println("Number of columns: "+ assetsMap.size());
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            for (String assetId : assetsMap.keySet()) {
                String asset = assetsMap.get(assetId);
                List<String> correctTerms = getAssociatedBusinessTerm(assetId);
                if(correctTerms.size()==0) {
                    searchExectorServiceUtil.submit(new SearchTask(asset, searcher, "", correctMatch, incorrectMatch, correctRecommendation, incorrectRecommendation, noMatch));
                }
                for (String correctTerm : correctTerms) {
                    searchExectorServiceUtil.submit(new SearchTask(asset, searcher, correctTerm, correctMatch, incorrectMatch, correctRecommendation, incorrectRecommendation, noMatch));
                }
            }
            searchExectorServiceUtil.close();
            engine.close();
            System.out.println("Correct Match: " + correctMatch);
            System.out.println("Incorrect Match: " + incorrectMatch);
            System.out.println("Correct Recommendations: " + correctRecommendation);
            System.out.println("Incorrect Recommendations: " + incorrectRecommendation);
            System.out.println("No Match: " + noMatch);

            float precision = (float)(correctMatch.get()+correctRecommendation.get())/(float)(correctMatch.get()+correctRecommendation.get()+incorrectMatch.get()+incorrectRecommendation.get());
            System.out.println("Precision: " + precision);
            System.out.println("Dict Values: "+dict_values);

            float recall = (float)(correctMatch.get()+correctRecommendation.get())/(float)dict_values;
            System.out.println("Recall: " + recall);
//            System.out.println("Airlinestermsearchtest.class");
//
////            Assert.assertEquals(java.util.Optional.of(correctMatch.get()), expectedCorrectMatch);
////            Assert.assertEquals(java.util.Optional.of(incorrectMatch.get()), expectedIncorrectMatch);
////            Assert.assertEquals(java.util.Optional.of(noMatch.get()), expectedNoMatch);
        }

    }

    public void read(String source) throws IOException {
        // Read Columns
        try (Reader reader = Files.newBufferedReader(Paths.get("src/test/resources/sampledata/columns_"+source+".csv"));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);)
        {
            boolean first = true;
            for (CSVRecord csvRecord : csvParser) {
                if (first) {
                    first = false;
                    continue;
                }
                // Accessing Values by Column Index
                String asset = csvRecord.get(0);

//                System.out.println("Record No - " + csvRecord.getRecordNumber());
//                System.out.println("---------------");
//                System.out.println("Asset : " + asset);
//                System.out.println("---------------\n\n");
                if (unique.contains(asset))
                        continue;
                    unique.add(asset);
                if(!colIdBtMap.containsKey(asset)) {
                    colIdBtMap.put(asset,new LinkedList<>());
                }
                    columnIdNameMap.put(asset, asset);
                }

            }

        try (Reader reader = Files.newBufferedReader(Paths.get("src/test/resources/sampledata/business_glossary_"+source+".csv"));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);)
        {
            boolean first = true;
            for (CSVRecord csvRecord : csvParser) {
                if (first) {
                    first = false;
                    continue;
                }
                // Accessing Values by Column Index
                String term = csvRecord.get(0);
                if (!term.trim().isEmpty()) {
                    terms.add(term);
                }

            }
        }

        try (Reader reader = Files.newBufferedReader(Paths.get("src/test/resources/sampledata/capture_"+source+".csv"));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);)
        {
            boolean first = true;
            for (CSVRecord csvRecord : csvParser) {
                if (first) {
                    first = false;
                    continue;
                }
                // Accessing Values by Column Index
                String asset = csvRecord.get(0).toLowerCase();
                String term = csvRecord.get(1);

//                System.out.println("Record No - " + csvRecord.getRecordNumber());
//                System.out.println("---------------");
//                System.out.println("Term : " + term);
//                System.out.println("---------------\n\n");
                if (!term.trim().isEmpty()) {
                    dict_values++;
                    colIdBtMap.get(asset).add(term);
                } else {
                    colsWithoutBt.add(asset);
                }

            }
        }



        System.out.println("Terms read: " + terms.size());
    }




    public List<String> getBussinessGlossaryTerms() {
        return new ArrayList<>(terms);
    }

    public Map<String, String> getColumnNames() {
        return columnIdNameMap;
    }

    public List<String> getAssociatedBusinessTerm(String assetIdentifier) {
        if (assetIdentifier == null) {
            throw new NullPointerException("Column has no BT associated with it.");
        }
        return colIdBtMap.get(assetIdentifier);
    }

}