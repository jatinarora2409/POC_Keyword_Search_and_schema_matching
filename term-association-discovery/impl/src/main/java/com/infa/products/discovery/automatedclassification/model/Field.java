package com.infa.products.discovery.automatedclassification.model;

public class Field {

	public enum Type {
		TITLE,
		TEXT
	}
	
	private String fieldName;
	
	private Type type;

	public Field(String fieldName, Type type) {
		super();
		this.fieldName = fieldName;
		this.type = type;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Type getType() {
		return type;
	}


}
