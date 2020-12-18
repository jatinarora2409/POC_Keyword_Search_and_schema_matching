package com.infa.products.discovery.automatedclassification.engine.api;


import com.infa.products.discovery.automatedclassification.exception.IndexWriterException;

import java.io.Closeable;
import java.io.IOException;

public interface SearchEngine<D> extends Closeable {

    IndexBuilder<D> getIndexBuilder() throws IndexWriterException;

    Searcher<D> getSearcher() throws IOException;

}
