package com.infa.products.discovery.automatedclassification.engine;

import com.infa.products.discovery.automatedclassification.engine.api.IndexBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngine;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.IndexWriterException;
import com.infa.products.discovery.automatedclassification.exception.InvalidAlignmentCandidatesException;
import com.infa.products.discovery.automatedclassification.model.*;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;
import com.infa.products.discovery.automatedclassification.util.ConfigUtil;
import com.infa.products.discovery.automatedclassification.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

public class BusinessTermSearchEngineTest {
    @BeforeClass
    public void setUp() {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);
    }

    @Test(testName = "testCandidateSearchSubsequenceMatch", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search for multiple queries", dataProvider = "getCandidateSearchSubsequenceMatchTestData")
    public void testCandidateSearchSubsequenceMatch(List<BusinessTerm> allTerms, String assetName,
                                                    List<BusinessTerm> expectedCandidateTerms)
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(allTerms.iterator());
        indexBuilder.close();
        List<SearchResult<BusinessTerm>> results = null;
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), assetName);
            Asset asset = new Asset("", assetName, AssetType.DATA_ELEMENT);
            results = searcher.search(asset, 10);

            for (SearchResult<BusinessTerm> result: results) {
                System.out.println(result.getDocument());
            }
        }
        engine.close();
    }

    @Test(testName = "testCandidateSearchFirstLetterMatch", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search for multiple queries", dataProvider = "getCandidateSearchFirstLetterMatchTestData")
    public void testCandidateSearchFirstLetterMatch(List<BusinessTerm> allTerms, String assetName,
                                                    List<BusinessTerm> expectedCandidateTerms)
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(allTerms.iterator());
        indexBuilder.close();
        List<SearchResult<BusinessTerm>> results = null;
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), assetName);
            Asset asset = new Asset("", assetName, AssetType.DATA_ELEMENT);
            results = searcher.search(asset, 10);
        }
        assert results.size() == expectedCandidateTerms.size();
        for (SearchResult<BusinessTerm> result : results) {
            System.out.println(result.getDocument());
        }
        engine.close();
    }


    @DataProvider(name = "getCandidateSearchSubsequenceMatchTestData", parallel = false)
    public static Object[][] getCandidateSearchSubsequenceMatchTestData() {
        BusinessTerm mtrcName = Utils.createBusinessTerm("BT1", "Metric Name", "BG");
        BusinessTerm srvcLineNbr = Utils.createBusinessTerm("BT2", "Service Line Number", "BG");
        BusinessTerm providerRel = Utils.createBusinessTerm("BT3", "Provider Relationship", "BG");
        BusinessTerm producerLic = Utils.createBusinessTerm("BT4", "Producer License", "BG");
        BusinessTerm pac = Utils.createBusinessTerm("BT5", "Provider Address Contact", "BG");
        BusinessTerm product = Utils.createBusinessTerm("BT6", "Product", "BG");
        BusinessTerm expId = Utils.createBusinessTerm("BT7", "Experience Identifier", "BG");
        BusinessTerm epId = Utils.createBusinessTerm("BT8", "Enterprise Provider Identifier", "BG");

        List<BusinessTerm> businessTerms = Arrays.asList(
                new BusinessTerm[]{mtrcName, srvcLineNbr, providerRel, producerLic, pac, product, expId, epId});

        return new Object[][]{new Object[]{businessTerms, "EP ID", Arrays.asList(expId, epId)},
                new Object[]{businessTerms, "PRL", Arrays.asList(providerRel, producerLic)},
                new Object[]{businessTerms, "PDC", Arrays.asList(pac, product, producerLic)},
                new Object[]{businessTerms, "MTRC NM", Arrays.asList(mtrcName)},
                new Object[]{businessTerms, "PRLX", Collections.EMPTY_LIST}};
    }

    @DataProvider(name = "getCandidateSearchFirstLetterMatchTestData", parallel = false)
    public static Object[][] getCandidateSearchFirstLetterMatchTestData() {
        BusinessTerm mtrcName = Utils.createBusinessTerm("BT1", "Metric Name", "BG");
        BusinessTerm srvcLineNbr = Utils.createBusinessTerm("BT2", "Service Line Number", "BG");
        BusinessTerm providerRel = Utils.createBusinessTerm("BT3", "Provider Relationship", "BG");
        BusinessTerm producerLic = Utils.createBusinessTerm("BT4", "Producer License", "BG");
        BusinessTerm pac = Utils.createBusinessTerm("BT5", "Provider Address Contact", "BG");
        BusinessTerm product = Utils.createBusinessTerm("BT6", "Product", "BG");
        BusinessTerm expId = Utils.createBusinessTerm("BT7", "Experience Identifier", "BG");
        BusinessTerm epId = Utils.createBusinessTerm("BT8", "Enterprise Provider Identifier", "BG");

        List<BusinessTerm> businessTerms = Arrays.asList(
                new BusinessTerm[]{mtrcName, srvcLineNbr, providerRel, producerLic, pac, product, expId, epId});

        return new Object[][]{new Object[]{businessTerms, "ETRIC NM", Collections.EMPTY_LIST},
                new Object[]{businessTerms, "REN", Collections.EMPTY_LIST}};
    }


    private void filterRepeatedSearchResults(List<SearchResult<BusinessTerm>> searchResults) {
        Set<String> queryBusinessTermsSet = new HashSet<>();
        Iterator<SearchResult<BusinessTerm>> searchResultIterator = searchResults.iterator();
        while (searchResultIterator.hasNext()) {
            SearchResult<BusinessTerm> searchResult = searchResultIterator.next();
            String keyString = searchResult.getDocument().toString();
            if (queryBusinessTermsSet.contains(keyString)) {
                searchResultIterator.remove();
            } else {
                queryBusinessTermsSet.add(keyString);
            }
        }
    }
}
