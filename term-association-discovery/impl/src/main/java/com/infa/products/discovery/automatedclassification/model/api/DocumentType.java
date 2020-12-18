package com.infa.products.discovery.automatedclassification.model.api;

import com.infa.products.discovery.automatedclassification.model.Field;

import java.util.List;

public interface DocumentType<D> {

	List<Field> getFields();
	
	DocumentReader<D> getDocumentReader();
	
	DocumentBuilder<D> getDocumentBuilder();
	
}
