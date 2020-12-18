package com.infa.products.discovery.automatedclassification.model;

public enum TermType {

    BG("com.infa.ldm.bg.inferredTerms",
            "com.infa.ldm.bg.associatedTerms",
            "com.infa.ldm.bg.infTerms"),

    AXON("com.infa.ldm.axon.inferredGlossaries",
            "com.infa.ldm.axon.associatedGlossaries",
            "com.infa.ldm.axon.infGlossaries");

    private final String inferredAssociationName;

    private final String acceptedAssociationName;

    private final String csvAttributeName;

    TermType(final String inferredAssociationName, final String acceptedAssociationName,
             final String csvAttributeName) {
        this.inferredAssociationName = inferredAssociationName;
        this.acceptedAssociationName = acceptedAssociationName;
        this.csvAttributeName = csvAttributeName;
    }

    public String getInferredAssociationName() {
        return inferredAssociationName;
    }

    public String getAcceptedAssociationName() {
        return acceptedAssociationName;
    }

    public String getCsvAttributeName() {
        return csvAttributeName;
    }
}
