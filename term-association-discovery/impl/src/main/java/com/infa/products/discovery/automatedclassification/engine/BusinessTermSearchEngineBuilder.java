package com.infa.products.discovery.automatedclassification.engine;

import com.infa.products.discovery.automatedclassification.engine.api.SearchEngine;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngineBuilder;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.model.BusinessTerm;
import com.infa.products.discovery.automatedclassification.model.Synonym;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;

import java.util.List;
import java.util.Objects;

public class BusinessTermSearchEngineBuilder implements SearchEngineBuilder<BusinessTerm> {

    private BusinessTermSearchEngine engine;

    @Override
    public SearchEngineBuilder<BusinessTerm> forDocumentType(DocumentType<BusinessTerm> type) {
        engine = new BusinessTermSearchEngine(type);
        return this;
    }

    @Override
    public SearchEngineBuilder<BusinessTerm> withSynonyms(Iterable<Synonym> synonyms) throws DocumentSearchException {
        Objects.requireNonNull(engine, "Document type not specified.");
        engine.buildSynonymMap(synonyms);
        return this;
    }

    @Override
    public SearchEngineBuilder<BusinessTerm> withStopWords(List<String> stopWords) throws DocumentSearchException {
        engine.setStopWordList(stopWords);
        return this;
    }

    @Override
    public SearchEngineBuilder<BusinessTerm> ignorePrefixes(List<String> prefixesToIgnore) throws DocumentSearchException {
        engine.setPrefixesToIgnore(prefixesToIgnore);
        return this;
    }

    @Override
    public SearchEngine<BusinessTerm> build() {
        return engine;
    }

}
