package com.infa.products.discovery.automatedclassification.engine.search;

import com.infa.products.discovery.automatedclassification.engine.BusinessTermSearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.IndexBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngine;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.IndexWriterException;
import com.infa.products.discovery.automatedclassification.model.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParentContextAssetTest {
    @Test
    public void parentContextAssetTest() throws DocumentSearchException, IndexWriterException, IOException {

        List<String> stopwords = new ArrayList<>();
        stopwords.add("for");
        stopwords.add("in");

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType());

        engineBuilder.withStopWords(stopwords);
        SearchEngine<BusinessTerm> engine = engineBuilder.build();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Employee ID").withType(TermType.AXON).build());

        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();
        indexBuilder.indexDocuments(businessTerms.iterator());

        Asset assetWithParent = new Asset("", "Id", AssetType.DATA_ELEMENT, "Employee");
        Asset asset = new Asset("", "id", AssetType.DATA_ELEMENT);

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        List<SearchResult<BusinessTerm>> results1 = searcher.search(assetWithParent, 10);
        Assert.assertEquals(results1.get(0).getMatchedString(), "Employee ID");

        List<SearchResult<BusinessTerm>> results2 = searcher.search(asset, 10);
        Assert.assertEquals(results2.size(), 0);
    }
}
