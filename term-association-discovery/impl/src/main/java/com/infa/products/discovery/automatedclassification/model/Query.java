package com.infa.products.discovery.automatedclassification.model;

public class Query {

	private String field;
	
	private String queryText;

	public Query(String field, String queryText) {
		super();
		this.field = field;
		this.queryText = queryText;
	}

	public String getField() {
		return field;
	}

	public String getQueryText() {
		return queryText;
	}
	
}
