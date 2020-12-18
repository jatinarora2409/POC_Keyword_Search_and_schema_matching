package com.infa.products.discovery.automatedclassification.model;

public class Synonym {

	private String phrase;
	
	private String synonymPhrase;

	public Synonym(String phrase, String synonymPhrase) {
		super();
		this.phrase = phrase;
		this.synonymPhrase = synonymPhrase;
	}

	public String getPhrase() {
		return phrase;
	}

	public String getSynonymPhrase() {
		return synonymPhrase;
	}
	
}
