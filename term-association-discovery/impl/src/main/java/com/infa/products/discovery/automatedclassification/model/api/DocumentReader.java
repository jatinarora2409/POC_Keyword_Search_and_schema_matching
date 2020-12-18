package com.infa.products.discovery.automatedclassification.model.api;

public interface DocumentReader<D> {

	Object getFieldValue(D doc, String field);
	
}
