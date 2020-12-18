package com.infa.products.discovery.automatedclassification.similarity.api;

public interface AlignmentScoringPolicy {

	AlignmentScorer getAlignmentScorer(String longForm, String shortForm);
	
}
