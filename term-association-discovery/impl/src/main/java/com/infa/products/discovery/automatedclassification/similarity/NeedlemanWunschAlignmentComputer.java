package com.infa.products.discovery.automatedclassification.similarity;

import com.infa.products.discovery.automatedclassification.exception.InvalidAlignmentCandidatesException;
import com.infa.products.discovery.automatedclassification.similarity.api.Alignment;
import com.infa.products.discovery.automatedclassification.similarity.api.AlignmentComputer;
import com.infa.products.discovery.automatedclassification.similarity.api.AlignmentScorer;

import java.util.List;
import java.util.Objects;

public class NeedlemanWunschAlignmentComputer implements AlignmentComputer {

    public interface MatchFunction {
        public boolean isMatch(char shortFormChar, char longFormChar);
    }

    public static final MatchFunction DEFAULT_MATCH_FUNCTION = new MatchFunction() {
        @Override
        public boolean isMatch(char shortFormChar, char longFormChar) {
            return longFormChar == shortFormChar;
        }

    };

    private static final short MAX_STRING_SIZE = 255;

    private final short[][] alignmentMatrix = new short[MAX_STRING_SIZE][MAX_STRING_SIZE];

    private final AlignmentScorer sf;

    private MatchFunction mf;

    public NeedlemanWunschAlignmentComputer(AlignmentScorer scorer, MatchFunction matchFunction) {
        this.sf = scorer;
        this.mf = matchFunction;
    }

    public void validateInputString(String inputString) {
        Objects.requireNonNull(inputString, "Long form for alignment can't be null.");
        if (inputString.length() >= MAX_STRING_SIZE) {
            throw new IllegalArgumentException("Long form [" + inputString + "] longer than max string size.");
        }
        if (inputString.isEmpty()) {
            throw new IllegalArgumentException("Long form for alignment can't be empty.");
        }
    }

    @Override
    synchronized public Alignment compute(String longForm, String shortForm)
            throws InvalidAlignmentCandidatesException {
        validateInputString(longForm);
        validateInputString(shortForm);
        if (!isSubsequence(shortForm, longForm, shortForm.length(), longForm.length())) {
            return null;
        }
        if (shortForm.length() > longForm.length()) {
            throw new InvalidAlignmentCandidatesException(
                    "Short form longer than long form [LF :(" + longForm + ") SF: (" + shortForm + ")]", longForm,
                    shortForm);
        }

        String longStr = "-" + longForm;
        String shortStr = "-" + shortForm;

        short gapScore = sf.getGapPenalty(longForm, shortForm);

        int numCols = longStr.length();
        int numRows = shortStr.length();

        for (int i = 0; i < numRows; i++) {
            alignmentMatrix[i][0] = (short) (i * gapScore);
        }
        for (int j = 0; j < numCols; j++) {
            alignmentMatrix[0][j] = (short) (j * gapScore);
        }

        List<Integer> indexMatchScores = sf.getMatchScore(longForm, shortForm);

        for (int i = 1; i < numRows; i++) {
            for (int j = 1; j < numCols; j++) {
                alignmentMatrix[i][j] = (short) Math.max(alignmentMatrix[i][j - 1] + gapScore,
                        alignmentMatrix[i - 1][j - 1]
                                + (mf.isMatch(shortStr.charAt(i), longStr.charAt(j)) ? indexMatchScores.get(j - 1)
                                : Short.MIN_VALUE));
            }
        }

        AbbreviationAlignment alignment = new AbbreviationAlignment(longForm, shortForm);
        int i = numRows - 1;
        int j = numCols - 1;

//        if (alignmentMatrix[i][j] < sf.getAcceptableScore(longForm, shortForm)) {
//            return null;
//        }

        while (i != 0 && j != 0) {
            if (alignmentMatrix[i][j] == (alignmentMatrix[i][j - 1] + gapScore)) {
                alignment.setGapAt(j - 1);
                j--;
            } else if (alignmentMatrix[i][j] == (alignmentMatrix[i - 1][j - 1]
                    + (mf.isMatch(shortStr.charAt(i), longStr.charAt(j)) ? indexMatchScores.get(j - 1)
                    : Short.MIN_VALUE))) {
                alignment.setMatchAt(j - 1);
                i--;
                j--;
            }
        }
        sf.scoreAlignment(alignment);
        if (alignment.getScore() == 0) {
            return null;
        }

        return alignment;
    }

    private boolean isSubsequence(String shortForm, String longForm, int shortFormLen, int longFormLen) {
        // Base cases
        if (shortFormLen == 0)
            return true;
        if (longFormLen == 0)
            return false;

        // If last characters of two strings are matching
        if (shortForm.charAt(shortFormLen - 1) == longForm.charAt(longFormLen - 1))
            return isSubsequence(shortForm, longForm, shortFormLen - 1, longFormLen - 1);

        // If last characters don't match
        return isSubsequence(shortForm, longForm, shortFormLen, longFormLen - 1);
    }
}
