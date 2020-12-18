package com.infa.products.discovery.automatedclassification.similarity.api;

import com.infa.products.discovery.automatedclassification.exception.InvalidAlignmentCandidatesException;

public interface AlignmentComputer {

    Alignment compute(String longForm, String shortForm) throws InvalidAlignmentCandidatesException;

}
