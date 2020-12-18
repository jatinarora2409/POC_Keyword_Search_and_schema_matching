package com.infa.products.discovery.automatedclassification.similarity;

import com.infa.products.discovery.automatedclassification.similarity.api.AlignmentComputer;

public class AlignmentComputerFactory {

	public static AlignmentComputer getAlignmentComputer(AbbreviationAlignmentScorer abbreviationAlignmentScorer) {
		return new NeedlemanWunschAlignmentComputer(abbreviationAlignmentScorer, NeedlemanWunschAlignmentComputer.DEFAULT_MATCH_FUNCTION);
	}
	
}
