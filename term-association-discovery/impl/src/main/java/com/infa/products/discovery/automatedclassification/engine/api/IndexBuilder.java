package com.infa.products.discovery.automatedclassification.engine.api;

import com.infa.products.discovery.automatedclassification.exception.IndexWriterException;

import java.io.Closeable;
import java.util.Iterator;

public interface IndexBuilder<D> extends Closeable {

    void indexDocuments(Iterator<D> docs) throws IndexWriterException;

}
