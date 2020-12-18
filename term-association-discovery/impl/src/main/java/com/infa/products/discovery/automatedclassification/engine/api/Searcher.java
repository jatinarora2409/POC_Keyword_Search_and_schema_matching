package com.infa.products.discovery.automatedclassification.engine.api;

import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.model.Asset;
import com.infa.products.discovery.automatedclassification.model.SearchResult;

import java.io.Closeable;
import java.util.List;

public interface Searcher<D> extends Closeable {

    List<SearchResult<D>> search(Asset asset, int maxResults) throws DocumentSearchException;

}
