package com.infa.products.discovery.automatedclassification.model;

public enum AssetType {
    DATA_SET("DATA_SET"),
    DATA_ELEMENT("DATA_ELEMENT");

    private String type;

    AssetType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
