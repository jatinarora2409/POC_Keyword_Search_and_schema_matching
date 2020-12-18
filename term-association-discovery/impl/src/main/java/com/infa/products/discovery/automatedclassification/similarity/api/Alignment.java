package com.infa.products.discovery.automatedclassification.similarity.api;


public interface Alignment {
    public static enum AlignmentResult {
        MATCH,
        GAP
    }

    String getLongForm();

    String getShortForm();

    int getNumWordsMatched();

    int getNumCharactersMatched();

    int getNumStopwordsMatched();

    int getNumStopwordCharsMatched();

    AlignmentResult resultAt(int index);

    float getScore();

    void setScore(float score);

    boolean equals(String alignedShortForm);

}
