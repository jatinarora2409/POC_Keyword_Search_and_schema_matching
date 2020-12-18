package com.infa.products.discovery.automatedclassification.dataset;

import com.infa.products.discovery.automatedclassification.engine.BusinessTermSearchEngineBuilder;
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
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

public class SpecialCharactersTest {


    @BeforeClass
    public void setUp() {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);
    }

    @Test(testName = "testSpecialChars", enabled = true, groups = {"TermSearchDatasetTests"}, description = "Test term search with Special characters")
    public void testSpecialChars()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {

        final List<BusinessTerm> businessTerms = Arrays
                .asList(
                        Utils.createBusinessTerm("BT1", "!@#^%-=+}{][:;_><?,~)(*&$/'.", "BG"),
                        Utils.createBusinessTerm("BT2", "Accounts Receivable (AR)", "BG"),
                        Utils.createBusinessTerm("BT3", "Social Security Number *SSN*", "AXON"),
                        Utils.createBusinessTerm("BT4", "Accumulated impairment $AI$", "AXON"),
                        Utils.createBusinessTerm("BT5", "Balance Sheet Total (BSN", "AXON"),
                        Utils.createBusinessTerm("BT6", "Contract Identifier CI)", "AXON"),
                        // Utils.createBusinessTerm("BT7", "*", "AXON"),
                        Utils.createBusinessTerm("BT8", "COLUMN1_!)&@W%W@", "AXON"),
                        // Utils.createBusinessTerm("BT9", "$", "AXON"),
                        Utils.createBusinessTerm("BT10", "^Prudential Portfolio", "AXON"),
                        Utils.createBusinessTerm("BT11", "Accounts Payable (AP)", "AXON")
                );
        List<Synonym> synonyms = new ArrayList<>();
        synonyms.add(new Synonym("ap", "accounts payable (ap)"));

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        engineBuilder.withStopWords(ENGLISH_STOP_WORD_SET);
        engineBuilder.withSynonyms(synonyms);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            for (BusinessTerm businessTerm : businessTerms) {
                final Asset asset = new Asset("", businessTerm.getName(), AssetType.DATA_ELEMENT);
                final List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
                assert results.size() == 1;
                assert businessTerm.getCatalogId().equals(results.get(0).getDocument().getCatalogId());
                if (results.get(0).getQuery().getQueryText().length() == 1) {
                    assert results.get(0).getSimilarityScore() == 90f;
                } else {
                    assert results.get(0).getSimilarityScore() == 100f;
                }
            }
            final Asset asset = new Asset("", "ap", AssetType.DATA_ELEMENT);
            final List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
            assert results.get(0).getSimilarityScore() == 100f;
        }
        engine.close();
    }
}
