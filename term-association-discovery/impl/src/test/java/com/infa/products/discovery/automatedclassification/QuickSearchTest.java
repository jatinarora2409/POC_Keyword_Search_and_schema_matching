package com.infa.products.discovery.automatedclassification;

import com.infa.products.discovery.automatedclassification.engine.BusinessTermSearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.IndexBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngine;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.IndexWriterException;
import com.infa.products.discovery.automatedclassification.model.*;
import com.infa.products.discovery.automatedclassification.util.ConfigUtil;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

@Test
public class QuickSearchTest {

    @BeforeClass
    public void setUp() {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);
    }


    @Test
    public void BOWTest() throws IOException, DocumentSearchException, IndexWriterException {

        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(new Synonym("united states", "america"));

        List<String> stopwords = new ArrayList<>();
        stopwords.add("of");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());
        engineBuilder.withSynonyms(synonymList);
        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        //businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("America Pincode Number").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Date of Submission").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Sub Date Time").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "DATE_SUB", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        for (SearchResult<BusinessTerm> result : searcher.search(asset, 10)) {
            System.out.println(result.getMatchedString());
        }

    }

    @Test
    public void BOWTest1() throws IOException, DocumentSearchException, IndexWriterException {

        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(new Synonym("entrprs", "enterprize"));
        synonymList.add(new Synonym("sz", "size"));

        List<String> stopwords = new ArrayList<>();
        stopwords.add("of");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());
        engineBuilder.withSynonyms(synonymList);
        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Enterprize Size").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "ENTRPRS_SZ", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);

        for (SearchResult<BusinessTerm> result : results) {
            System.out.println(result.getMatchedString());
        }

    }


    @Test
    public void BOWTest2() throws DocumentSearchException, IndexWriterException, IOException {
        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(new Synonym("entrprs", "enterprize"));
        synonymList.add(new Synonym("sz", "size"));

        List<String> stopwords = new ArrayList<>();
        stopwords.add("of");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());
        engineBuilder.withSynonyms(synonymList);
        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("abc abc abc").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "abc_abc", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);

        for (SearchResult<BusinessTerm> result : results) {
            System.out.println(result.getMatchedString());
        }
    }
    @Test
    public void BOWTest3() throws DocumentSearchException, IndexWriterException, IOException {
        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(new Synonym("entrprs", "enterprize"));
        synonymList.add(new Synonym("sz", "size"));

        List<String> stopwords = new ArrayList<>();
        stopwords.add("of");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());
        engineBuilder.withSynonyms(synonymList);
        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("(REIT)").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "REIT", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);

        for (SearchResult<BusinessTerm> result : results) {
            System.out.println(result.getMatchedString());
        }
    }
    @Test
    public void BOWTest4() throws DocumentSearchException, IndexWriterException, IOException {
        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(new Synonym("tp", "turning point"));
        synonymList.add(new Synonym("tp", "tradition plus"));

        List<String> stopwords = new ArrayList<>();
        stopwords.add("of");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());
        engineBuilder.withSynonyms(synonymList);
        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("(TP)").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "TP", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);

        for (SearchResult<BusinessTerm> result : results) {
            System.out.println(result.getMatchedString());
        }
    }
    @Test
    public void BOWTest5() throws DocumentSearchException, IndexWriterException, IOException {
        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(new Synonym("id", "identity"));
        synonymList.add(new Synonym("id", "identifier"));

        List<String> stopwords = new ArrayList<>();
        stopwords.add("of");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());
        engineBuilder.withSynonyms(synonymList);
        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("(id)").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "id", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);

        for (SearchResult<BusinessTerm> result : results) {
            System.out.println(result.getMatchedString());
        }
    }
    @Test
    public void BOWTest6() throws DocumentSearchException, IndexWriterException, IOException {
        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(new Synonym("id", "identity"));
        synonymList.add(new Synonym("id", "identifier"));

        List<String> stopwords = new ArrayList<>();
        stopwords.add("of");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());
        engineBuilder.withSynonyms(synonymList);
        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Instrument Type").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "Instrument", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);

        for (SearchResult<BusinessTerm> result : results) {
            System.out.println(result.getMatchedString());
        }
    }

    @Test
    public void BOWTest7() throws DocumentSearchException, IndexWriterException, IOException {
        List<Synonym> synonymList = new ArrayList<>();

        for(int i=0;i<250;i++) {
            synonymList.add(new Synonym("id", "identity"+i));
        }

        List<String> stopwords = new ArrayList<>();
        stopwords.add("of");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());
        engineBuilder.withSynonyms(synonymList);
        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("id").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "new id", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);

        for (SearchResult<BusinessTerm> result : results) {
            System.out.println(result.getMatchedString());
        }
    }

    @Test
    public void BOWTest8() throws DocumentSearchException, IndexWriterException, IOException {

        List<String> stopwords = new ArrayList<>();
        stopwords.add("for");
        stopwords.add("in");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());

        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Reduction Cost (Percent)").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "percent_reduction_cost", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
        String actualMatch="";
        String expectedMatch = "Reduction Cost (Percent)";
        for (SearchResult<BusinessTerm> result : results) {
            actualMatch = result.getMatchedString();
            System.out.println(result.getMatchedString());
        }
        Assert.assertEquals(actualMatch, expectedMatch);
    }

    @Test
    public void BOWTest9() throws DocumentSearchException, IndexWriterException, IOException {

        List<String> stopwords = new ArrayList<>();
        stopwords.add("for");
        stopwords.add("in");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());

        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Cholesterol medium-low").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "medium_low_cholesterol", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
        String actualMatch="";
        String expectedMatch = "Cholesterol medium-low";
        for (SearchResult<BusinessTerm> result : results) {
            actualMatch = result.getMatchedString();
            System.out.println(result.getMatchedString());
        }
        Assert.assertEquals(actualMatch, expectedMatch);
    }

    @Test
    public void MissingWordsTest1() throws DocumentSearchException, IndexWriterException, IOException {

        List<String> stopwords = new ArrayList<>();
        stopwords.add("for");
        stopwords.add("in");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());

        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("alternate identifier code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("ESG quartile code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Instrument type code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("metric identifier code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("drug related group code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("hmo site code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("subgroup level code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("ldl code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("high density lipoprotein").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "drg_rltd_grp", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
        String actualMatch="";
        String expectedMatch = "drug related group code";
        for (SearchResult<BusinessTerm> result : results) {
            actualMatch = result.getMatchedString();
            System.out.println(result.getMatchedString());
        }
        Assert.assertEquals(actualMatch, expectedMatch);
    }

    @Test
    public void MissingWordsTest2() throws DocumentSearchException, IndexWriterException, IOException {

        List<String> stopwords = new ArrayList<>();
        stopwords.add("for");
        stopwords.add("in");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());

        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("alternate identifier code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("ESG quartile code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Instrument type code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("metric identifier code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("drug related group code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("hmo site code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("subgroup level code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("ldl code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("high density lipoprotein").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "group related code", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
        String actualMatch="";
        String expectedMatch = "";
        for (SearchResult<BusinessTerm> result : results) {
            actualMatch = result.getMatchedString();
            System.out.println(result.getMatchedString());
        }
        Assert.assertEquals(actualMatch, expectedMatch);
    }

    @Test
    public void MissingWordsTest3() throws DocumentSearchException, IndexWriterException, IOException {
        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(new Synonym("grp", "group"));
        synonymList.add(new Synonym("drg", "drug"));
        synonymList.add(new Synonym("rltd", "related"));

        List<String> stopwords = new ArrayList<>();
        stopwords.add("for");
        stopwords.add("in");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());
        engineBuilder.withSynonyms(synonymList);
        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("alternate identifier code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("ESG quartile code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Instrument type code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("metric identifier code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("drug related group code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("hmo site code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("subgroup level code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("ldl code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("high density lipoprotein").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset asset = new Asset("", "grp rltd drg", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);
        String actualMatch="";
        String expectedMatch = "drug related group code";
        for (SearchResult<BusinessTerm> result : results) {
            actualMatch = result.getMatchedString();
            System.out.println(result.getMatchedString());
        }
        Assert.assertEquals(actualMatch, expectedMatch);
    }

    @Test
    public void AssetWithSpecialCharactersTest() throws DocumentSearchException, IndexWriterException, IOException {
        List<Synonym> synonymList = new ArrayList<>();
        synonymList.add(new Synonym("nm", "(name)"));
        synonymList.add(new Synonym("qurt", "(quartile)"));
        synonymList.add(new Synonym("inst", "(instrument)"));
        synonymList.add(new Synonym("scr", "(score)"));
        synonymList.add(new Synonym("typ", "(type)"));
        synonymList.add(new Synonym("lvl", "(level)"));

        List<String> stopwords = new ArrayList<>();
        stopwords.add("for");
        stopwords.add("in");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());
        engineBuilder.withSynonyms(synonymList);
        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("alternate identifier").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("career level subgroup code").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("metric name").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("esg score quartile").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("instrument type").withType(TermType.AXON).build());
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("low level density").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        List<Asset> assetList = new ArrayList<>();
        assetList.add(new Asset("", "alt_(id)", AssetType.DATA_ELEMENT));
        assetList.add(new Asset("", "subgroup level career (code)", AssetType.DATA_ELEMENT));
        assetList.add(new Asset("", "mtrc_nm", AssetType.DATA_ELEMENT));
        assetList.add(new Asset("", "scr_esg_qurt", AssetType.DATA_ELEMENT));
        assetList.add(new Asset("", "inst_(typ)", AssetType.DATA_ELEMENT));
        assetList.add(new Asset("", "(lvl)_low_density", AssetType.DATA_ELEMENT));

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        for (Asset asset:assetList) {
            List<SearchResult<BusinessTerm>> results = searcher.search(asset, 10);

            for (SearchResult<BusinessTerm> result : results) {
                System.out.println(result.getMatchedString()+" "+result.getSimilarityScore());
            }
        }
    }

}
