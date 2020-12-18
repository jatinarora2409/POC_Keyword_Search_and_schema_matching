package com.infa.products.discovery.automatedclassification.engine.api;

import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.model.Synonym;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;

import java.util.List;

public interface SearchEngineBuilder<D> {

    SearchEngineBuilder<D> forDocumentType(DocumentType<D> type);

    SearchEngineBuilder<D> withSynonyms(Iterable<Synonym> synonyms) throws DocumentSearchException;

    SearchEngineBuilder<D> withStopWords(List<String> stopWords) throws DocumentSearchException;

    SearchEngineBuilder<D> ignorePrefixes(List<String> prefixesToIgnore) throws DocumentSearchException;

    SearchEngine<D> build();

}
