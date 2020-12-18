package com.infa.products.discovery.automatedclassification.model.api;

public interface DocumentBuilder<D> {

	DocumentBuilder<D> withField(String field, Object value);
	
	D build();
	
}
