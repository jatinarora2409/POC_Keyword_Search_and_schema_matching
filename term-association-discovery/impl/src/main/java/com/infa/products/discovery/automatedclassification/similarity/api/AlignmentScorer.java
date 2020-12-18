package com.infa.products.discovery.automatedclassification.similarity.api;

import java.util.List;

public interface AlignmentScorer {

	List<Integer> getMatchScore(String longForm, String shortForm);
	
	short getGapPenalty(String longForm, String shortForm);

	short getAcceptableScore(String longForm, String shortForm);
	
	void scoreAlignment(Alignment alignment);

}
