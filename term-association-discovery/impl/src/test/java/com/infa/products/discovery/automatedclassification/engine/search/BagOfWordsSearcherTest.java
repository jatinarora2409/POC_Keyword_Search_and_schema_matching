package com.infa.products.discovery.automatedclassification.engine.search;

import com.infa.products.discovery.automatedclassification.engine.BusinessTermSearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.IndexBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngine;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.IndexWriterException;
import com.infa.products.discovery.automatedclassification.model.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BagOfWordsSearcherTest {

    @Test
    public void BOWSearchTest() throws DocumentSearchException, IndexWriterException, IOException {
        List<String> stopwords = new ArrayList<>();
        List<Synonym> synonyms = new ArrayList<>();
        synonyms.add(new Synonym("entrprs", "enterprize"));

        SearchEngineBuilder<BusinessTerm> engineBuilder = new BusinessTermSearchEngineBuilder();
        engineBuilder.forDocumentType(new BusinessTermType())
                .withStopWords(stopwords)
                .withSynonyms(synonyms);

        SearchEngine<BusinessTerm> engine = engineBuilder.build();
        IndexBuilder<BusinessTerm> indexBuilder = engine.getIndexBuilder();

        List<BusinessTerm> businessTerms = new ArrayList<>();
        businessTerms.add(BusinessTerm.builder().withCatalogId("").withName("Enterprize Size").withType(TermType.AXON).build());
        indexBuilder.indexDocuments(businessTerms.iterator());

        Searcher<BusinessTerm> searcher = engine.getSearcher();

        Asset asset = new Asset("", "ENTRPRS_SZ", AssetType.DATA_ELEMENT);
        List<SearchResult<BusinessTerm>> results = searcher.search(asset, 50);

        for (SearchResult<BusinessTerm>result : results) {
            System.out.println(result.getDocument());
        }

    }


}
