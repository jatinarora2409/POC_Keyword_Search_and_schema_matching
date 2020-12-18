package com.infa.products.discovery.automatedclassification;

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
import java.util.*;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

public class BusinessTermSearchTest {

    @BeforeClass
    public void setUp() {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);
    }

    List<BusinessTerm> businessTerms = Arrays
            .asList(new BusinessTerm[]{Utils.createBusinessTerm("BT1", "Social Security Number", "BG"),
                    Utils.createBusinessTerm("BT2", "Business Owned Table Homegrown Cross Reference", "BG"),
                    Utils.createBusinessTerm("BT3", "Enterprise Provider Individual Provider Title", "AXON")});

    List<Synonym> synonyms = Arrays.asList(new Synonym[]{new Synonym("xref", "cross reference")});

    @Test(testName = "testBusinessTermSearch", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search")
    public void termSearchTest()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "SSN");
            Asset asset = new Asset("", "SSN", AssetType.DATA_ELEMENT);
            List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
            assert results.size() == 1;
        }
    }

    @Test(testName = "testBusinessTermSearch", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search")
    public void termSearchWithAliasTest()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {

        final Set<String> alias1 = new HashSet<>(Arrays.asList("Selective Service Number"));
        final Set<String> alias2 = new HashSet<>(Arrays.asList("Secure Server Network", "ssn"));
        final Set<String> alias3 = new HashSet<>(Arrays.asList("Standard Study Number", "ssndata"));
        final List<BusinessTerm> businessTerms = Arrays
                .asList(new BusinessTerm[]{
                        Utils.createBusinessTerm("BT1", "Social Security Number", alias1, "BG"),
                        Utils.createBusinessTerm("BT2", "Business Owned Table Homegrown Cross Reference", alias2, "BG"),
                        Utils.createBusinessTerm("BT3", "Enterprise Provider Individual Provider Title", alias3, "AXON")
                });
        List<Synonym> synonyms = new ArrayList<>();
        synonyms.add(new Synonym("ssn", "standard study number"));

        final SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        final DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        engineBuilder.withSynonyms(synonyms);
        final SearchEngine<BusinessTerm> engine = engineBuilder.build();
        final IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
//            final Query query1 = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "SSN");
//            final Query query2 = new Query(BusinessTermType.BUSINESS_TERM_SYNONYMS_FIELD.getFieldName(), "SSN");
            final Asset asset1 = new Asset("", "SSN", AssetType.DATA_ELEMENT);
            final Asset asset2 = new Asset("", "SSN", AssetType.DATA_ELEMENT);
            final List<SearchResult<BusinessTerm>> results1 = searcher.search(asset1, 10);
            final List<SearchResult<BusinessTerm>> results2 = searcher.search(asset2, 10);
            final List<SearchResult<BusinessTerm>> results = Utils.merge(results1, results2, 10);
        }
    }

    @Test(testName = "testBusinessTermSearch", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search")
    public void termSearchWithoutAliasTest()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {

        final List<BusinessTerm> businessTerms = Arrays
                .asList(new BusinessTerm[]{
                        Utils.createBusinessTerm("BT1", "Social Security Number", "BG"),
                        Utils.createBusinessTerm("BT2", "Business Owned Table Homegrown Cross Reference", "BG"),
                        Utils.createBusinessTerm("BT3", "Enterprise Provider Individual Provider Title", "AXON")
                });

        final SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        final DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        final SearchEngine<BusinessTerm> engine = engineBuilder.build();
        final IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
//            final Query query1 = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "SSN");
//            final Query query2 = new Query(BusinessTermType.BUSINESS_TERM_SYNONYMS_FIELD.getFieldName(), "SSN");
            final Asset asset1 = new Asset("", "SSN", AssetType.DATA_ELEMENT);
            final Asset asset2 = new Asset("", "SSN", AssetType.DATA_ELEMENT);
            final List<SearchResult<BusinessTerm>> results1 = searcher.search(asset1, 10);
            final List<SearchResult<BusinessTerm>> results2 = searcher.search(asset2, 10);
            final List<SearchResult<BusinessTerm>> results = Utils.merge(results1, results2, 10);
            // Check if the correct number of results are returned
        }
    }

    @Test(testName = "testBusinessTermSearchMultipleQueries", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search for multiple queries")
    public void termSearchMultipleQueriesTest()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "SSN");
            Asset asset = new Asset("", "SSN", AssetType.DATA_ELEMENT);
            List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
            assert results.size() == 1;
            //query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "EP IPT");
            asset = new Asset("", "EP IPT", AssetType.DATA_ELEMENT);
            results = searcher.search(asset, 10);
            assert results.size() == 1;
        }
    }

    @Test(testName = "testBusinessTermSearchWithSynonyms", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search with synonyms")
    public void termSearchWithSynonymsTest()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        engineBuilder.withSynonyms(synonyms);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "BOT_HMGRWN_XREF");
            Asset asset = new Asset("", "BOT_HMGRWN_XREF", AssetType.DATA_ELEMENT);
            List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
            assert results.size() == 1;
            assert results.get(0).getDocument().getCatalogId().equals(businessTerms.get(1).getCatalogId());
        }
    }

    @Test(testName = "testBusinessTermSearchWithSynonyms", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search with synonyms")
    public void termSearchWithSynonymsTest1()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        List<BusinessTerm> businessTerms = Arrays
                .asList(new BusinessTerm[]{Utils.createBusinessTerm("BT1", "Chartfield Cross Reference Business Unit Code", "BG")});
        List<Synonym> synonyms = Arrays.asList(new Synonym[]{new Synonym("cfxref", "chartfield cross reference")});
        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        engineBuilder.withSynonyms(synonyms);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "CFXREF_BU_CD");
            Asset asset = new Asset("", "CFXREF_BU_CD", AssetType.DATA_ELEMENT);
            List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
            assert results.size() == 1;
            assert results.get(0).getDocument().getCatalogId().equals(businessTerms.get(0).getCatalogId());
        }
    }

    @Test(testName = "termSearchWithMultiWordSynonymsTest", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search with multi word synonyms")
    public void termSearchWithMultiWordSynonymsTest()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        List<BusinessTerm> businessTerms = Arrays
                .asList(new BusinessTerm[]{Utils.createBusinessTerm("BT1", "Cost Per Unit", "BG")});
        List<Synonym> synonyms = Arrays.asList(new Synonym[]{new Synonym("cost per share", "cost per share"),
                new Synonym("cost per share", "cost per unit"), new Synonym("cost per unit", "cost per unit"),
                new Synonym("cost per unit", "cost per share")});

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        engineBuilder.withSynonyms(synonyms);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "COST_PER_SHARE");
            Asset asset = new Asset("", "COST_PER_SHARE", AssetType.DATA_ELEMENT);
            List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
            assert results.size() == 1;
            assert results.get(0).getDocument().getCatalogId().equals(businessTerms.get(0).getCatalogId());
        }
    }

    @Test(testName = "termSearchWithUnderscoreInBusinessTermName", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search with special letters in business term name")
    public void termSearchWithUnderscoreInBusinessTermName()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        List<BusinessTerm> businessTerms = Arrays
                .asList(new BusinessTerm[]{Utils.createBusinessTerm("BT1", "Home_Src_Billg_Tax_Id", "BG"),
                        Utils.createBusinessTerm("BT2", "Home_Src_Billg_Tax_Id_Type_Cd", "BG")});

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        engineBuilder.withSynonyms(synonyms);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(),"HOME_SRC_BILLG_TAX_ID_TYPE_CD");
            Asset asset = new Asset("", "HOME_SRC_BILLG_TAX_ID_TYPE_CD", AssetType.DATA_ELEMENT);
            List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
            assert results.size() == 1;
            assert results.get(0).getDocument().getCatalogId().equals(businessTerms.get(1).getCatalogId());
        }
        engine.close();
    }

    @Test(testName = "termSearchRankingTest", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search with special letters in business term name")
    public void termSearchRankingTest()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        List<BusinessTerm> businessTerms = Arrays
                .asList(new BusinessTerm[]{Utils.createBusinessTerm("BT1", "Control Cholesterol Number", "BG"),
                        Utils.createBusinessTerm("BT2", "Contract Name", "BG")});

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "CNTRCT_NM");
            Asset asset = new Asset("", "CNTRCT_NM", AssetType.DATA_ELEMENT);
            List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
            assert results.size() == 2;

            if (results.get(0).getDocument().getCatalogId().equals(businessTerms.get(1).getCatalogId())) {
                assert results.get(0).getSimilarityScore() > results.get(1).getSimilarityScore();
            } else {
                assert results.get(0).getSimilarityScore() < results.get(1).getSimilarityScore();
            }
        }
        engine.close();
    }

    @Test(testName = "termSearchWithSingleWordSynonymTest", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search with single word synonym")
    public void termSearchWithSingleWordSynonymTest()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        List<BusinessTerm> businessTerms = Arrays
                .asList(new BusinessTerm[]{Utils.createBusinessTerm("BT1", "Total Drug Spend Applied Amount", "BG"),
                        Utils.createBusinessTerm("BT2", "Contract Name", "BG")});
        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        List<Synonym> synonyms = Arrays.asList(new Synonym[]{new Synonym("aplyd", "applied")});
        engineBuilder.withSynonyms(synonyms);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "TOTL_DRUG_SPEND_APLYD_AMT");
            Asset asset = new Asset("", "TOTL_DRUG_SPEND_APLYD_AMT", AssetType.DATA_ELEMENT);
            List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
            assert results.size() == 1;
            assert results.get(0).getDocument().getCatalogId().equals(businessTerms.get(0).getCatalogId());
        }
        engine.close();
    }

    @Test(testName = "termSearchWithPrefixesToIgnoreTest", enabled = true, groups = {
            "TermSearchTests"}, description = "Test term search with prefixes to ignore")
    public void termSearchWithPrefixesToIgnoreTest()
            throws InvalidAlignmentCandidatesException, IndexWriterException, IOException, DocumentSearchException {
        String csv = "iod,dp,dsg";
        String[] prefixes = csv.split(",");
        List<BusinessTerm> businessTerms = Arrays
                .asList(new BusinessTerm[]{Utils.createBusinessTerm("BT1", "Account Code", "BG"),
                        Utils.createBusinessTerm("BT2", "Value Frequency", "BG"),
                        Utils.createBusinessTerm("BT3", "Organization Id", "BG")});
        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        DocumentType<BusinessTerm> docType = new BusinessTermType();
        engineBuilder.forDocumentType(docType);
        engineBuilder.ignorePrefixes(Arrays.asList(prefixes));
        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());
        indexBuilder.close();
        try (Searcher<BusinessTerm> searcher = engine.getSearcher()) {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "DSG_ACCT_CD");
            Asset asset = new Asset("", "DSG_ACCT_CD", AssetType.DATA_ELEMENT);
            List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
            assert results.size() == 1;
            assert results.get(0).getDocument().getCatalogId().equals(businessTerms.get(0).getCatalogId());

            //query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "DP_VF");
            asset = new Asset("", "DP_VF", AssetType.DATA_ELEMENT);
            results = searcher.search(asset, 10);
            assert results.size() == 1;
            assert results.get(0).getDocument().getCatalogId().equals(businessTerms.get(1).getCatalogId());

            //query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), "IOD_ORG_ID");
            asset = new Asset("", "IOD_ORG_ID", AssetType.DATA_ELEMENT);
            results = searcher.search(asset, 10);
            assert results.size() == 1;
            assert results.get(0).getDocument().getCatalogId().equals(businessTerms.get(2).getCatalogId());
        }
        engine.close();
    }

}

