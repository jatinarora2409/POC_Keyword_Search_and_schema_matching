package com.infa.products.discovery.automatedclassification.similarity;

import com.infa.products.discovery.automatedclassification.similarity.api.Alignment;
import com.infa.products.discovery.automatedclassification.util.Pair;

import java.util.*;

public class AbbreviationAlignment implements Alignment {

    private String fixedLongForm;

    private String shortForm;

    private AlignmentResult[] result;

    private int numCharsMatched = 0;

    private int numWordsMatched = 0;

    private int numStopwordsMatched = 0;

    private int numStopwordCharsMathced = 0;

    private Set<Integer> stopWordIndices;

    private Set<Integer> stopWordCharIndices;

    private float score = 0;

    public AbbreviationAlignment(String fixedLongForm, String shortForm) {
        this.fixedLongForm = fixedLongForm;
        this.shortForm = shortForm;
        this.result = new AlignmentResult[this.fixedLongForm.length()];
        stopWordIndices = new HashSet<>();
        stopWordCharIndices = new HashSet<>();
    }

    private List<Pair<Integer, String>> getWordsWithIndex(String value) {
        String[] words = value.split("\\s+");
        List<Pair<Integer, String>> wordIndexPairs = new ArrayList<>();
        char[] characterArray = value.toCharArray();
        int j = 0;
        for (int i = 0; i < characterArray.length; i++) {
            if (characterArray[i] == ' ' && characterArray[i - 1] == ' ') {
                continue;
            }
            if (i == 0 || characterArray[i - 1] == ' ') {
                wordIndexPairs.add(new Pair(i, words[j]));
                j++;
            }
        }
        return wordIndexPairs;
    }

    public AbbreviationAlignment(Alignment alignment) {
        fixedLongForm = alignment.getLongForm();
        shortForm = alignment.getShortForm();
        result = new AlignmentResult[fixedLongForm.length()];
        for (int i = 0; i < fixedLongForm.length(); i++) {
            result[i] = alignment.resultAt(i);
        }
    }

    public void setGapAt(int i) {
        this.result[i] = AlignmentResult.GAP;
    }

    public void setMatchAt(int i) {
        this.result[i] = AlignmentResult.MATCH;
        if (i == 0 || this.fixedLongForm.charAt(i - 1) == ' ') {
            numWordsMatched++;
            if (stopWordIndices.contains(i)) {
                numStopwordsMatched++;
            }
        }

        if (stopWordCharIndices.contains(i)) {
            numStopwordCharsMathced++;
        }
        numCharsMatched++;
    }

    @Override
    public int getNumCharactersMatched() {
        return numCharsMatched;
    }

    @Override
    public int getNumWordsMatched() {
        return numWordsMatched;
    }

    public int getNumStopwordsMatched() {
        return numStopwordsMatched;
    }

    public int getNumStopwordCharsMatched() {
        return numStopwordCharsMathced;
    }

    @Override
    public String getLongForm() {
        return fixedLongForm;
    }

    @Override
    public String getShortForm() {
        return shortForm;
    }

    @Override
    public AlignmentResult resultAt(int index) {
        return result[index];
    }

    @Override
    public float getScore() {
        return score;
    }

    @Override
    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fixedLongForm == null) ? 0 : fixedLongForm.hashCode());
        result = prime * result + Arrays.hashCode(this.result);
        result = prime * result + ((shortForm == null) ? 0 : shortForm.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbbreviationAlignment other = (AbbreviationAlignment) obj;
        if (fixedLongForm == null) {
            if (other.fixedLongForm != null)
                return false;
        } else if (!fixedLongForm.equals(other.fixedLongForm))
            return false;
        if (!Arrays.equals(result, other.result))
            return false;
        if (shortForm == null) {
            if (other.shortForm != null)
                return false;
        } else if (!shortForm.equals(other.shortForm))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(fixedLongForm);
        buffer.append("\n");
        for (int i = 0; i < result.length; i++) {
            buffer.append(result[i] == AlignmentResult.MATCH ? fixedLongForm.charAt(i) : '-');
        }
        buffer.append("\n\n");
        buffer.append("Words Matched : " + numWordsMatched);
        buffer.append("\n");
        buffer.append("Characters Matched : " + numCharsMatched);
        return buffer.toString();
    }

    @Override
    public boolean equals(String alignedShortForm) {
        for (int i = 0; i < this.fixedLongForm.length(); i++) {
            if (fixedLongForm.charAt(i) == alignedShortForm.charAt(i)) {
                if (result[i] != AlignmentResult.MATCH) {
                    return false;
                }
            } else if (fixedLongForm.charAt(i) != alignedShortForm.charAt(i)) {
                if (result[i] != AlignmentResult.GAP) {
                    return false;
                }
            }
        }
        return true;
    }
}
