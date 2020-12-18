package com.infa.products.discovery.automatedclassification.similarity;

import com.infa.products.discovery.automatedclassification.engine.search.TfIdfTermScorer;
import com.infa.products.discovery.automatedclassification.similarity.api.Alignment;
import com.infa.products.discovery.automatedclassification.similarity.api.AlignmentScorer;

import java.util.*;

import static com.infa.products.discovery.automatedclassification.util.TermAssociationHelper.*;


// TODO: Fix this class
public final class AbbreviationAlignmentScorer implements AlignmentScorer {

    private static final int CHAR_MATCH_SCORE = getCharMatchScore();
    private static final int STOP_WORD_MATCH_BOOST = getStopWordMatchBoost();
    private static final int WORD_MATCH_BOOST = getWordMatchBoost();
    private static final char WHITESPACE_CHAR = ' ';

    private final Set<String> stopWordSet;

    TfIdfTermScorer tfIdfTermScorer;

    public AbbreviationAlignmentScorer(final Set<String> stopWordSet, TfIdfTermScorer tfIdfTermScorer) {
        this.tfIdfTermScorer = tfIdfTermScorer;
        this.stopWordSet = stopWordSet;
    }

    @Override
    public List<Integer> getMatchScore(String longForm, String shortForm) {
        List<Integer> indexMatchScores = new ArrayList<>();
        String[] words = longForm.split("\\s+");
        Set<Integer> stopWordIndices = new HashSet<>();
        for (String word : words) {
            if (stopWordSet.contains(word)) {
                stopWordIndices.add(longForm.indexOf(word));
            }
        }
        for (int i = 0; i < longForm.length(); i++) {
            if (stopWordIndices.contains(i)) {
                indexMatchScores.add(CHAR_MATCH_SCORE + STOP_WORD_MATCH_BOOST);
            } else if (i == 0 || longForm.charAt(i - 1) == WHITESPACE_CHAR) {
                indexMatchScores.add(CHAR_MATCH_SCORE + WORD_MATCH_BOOST);
            } else {
                indexMatchScores.add(CHAR_MATCH_SCORE);
            }
        }
        return indexMatchScores;
    }

    @Override
    public short getGapPenalty(String longForm, String shortForm) {
        return 0;
    }

    /*
     * Only accept alignments where all characters in the short form are aligned and
     * at least the first letters of words other than stop words are aligned.
     */
    @Override
    public short getAcceptableScore(String longForm, String shortForm) {
        String[] words = longForm.split("\\s+");
        int stopWordCount = 0;
        List<Integer> stopWordIndices = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if (stopWordSet.contains(words[i])) {
                stopWordCount++;
                stopWordIndices.add(longForm.indexOf(words[i]));
            }
        }
        int acceptableScore = shortForm.length() * CHAR_MATCH_SCORE + (words.length - stopWordCount) * WORD_MATCH_BOOST;
        return (short) acceptableScore;
    }

    @Override
    public void scoreAlignment(Alignment alignment) {

        List<String> matchedWordsExcludingStopwords = new ArrayList<>();
        List<String> longFormWordsExcludingStopwords = new ArrayList<>();
        List<String> longFormStopWords = new ArrayList<>();
        String longForm = alignment.getLongForm();
        String shortForm = alignment.getShortForm();
        int numMatchedStopwordChars = 0;
        int numLongFormStopwordChars = 0;

        // check if alignment is valid
        for (int i = 0; i < longForm.length(); i++) {
            if (isNull(alignment.resultAt(i))) {
                alignment.setScore(0f);
                return;
            }
        }

        /*
         * Check if an alignment is valid. Alignments where a letter in a word matches
         * without matching the first letter of that word are invalid.
         */
        boolean currentWordMatches = false;
        for (int i = 0; i < longForm.length(); i++) {

            if (isNull(alignment.resultAt(i))) {
                alignment.setScore(0f);
                return;
            }

            if (i == 0 || longForm.charAt(i - 1) == WHITESPACE_CHAR) {
                /*
                 * First letter of a word.
                 */
                if (alignment.resultAt(i).equals(Alignment.AlignmentResult.MATCH)) {
                    currentWordMatches = true;
                } else {
                    currentWordMatches = false;
                }
            } else {
                /*
                 * Letters other than first letter of a word.
                 */
                if (!currentWordMatches && alignment.resultAt(i).equals(Alignment.AlignmentResult.MATCH)) {
                    /*
                     * Invalid alignment.
                     */
                    alignment.setScore(0);
                    return;
                }
            }
        }

        int i = 0;
        currentWordMatches = false;
        String word;
        // number of chars matched including stopwords chars
        int numMatchedCharsExcludingStopword = 0;


        while (i < longForm.length()) {
            StringBuilder sb = new StringBuilder();
            currentWordMatches = alignment.resultAt(i).equals(Alignment.AlignmentResult.MATCH) ? true : false;
            int numCurrentWordMatchedChars = 0;
            while (i < longForm.length() && longForm.charAt(i) != WHITESPACE_CHAR) {
                sb.append(longForm.charAt(i));
                if (alignment.resultAt(i).equals(Alignment.AlignmentResult.MATCH)) {
                    numCurrentWordMatchedChars++;
                }
                i++;
            }
            i++;

            word = sb.toString();

            if (stopWordSet.contains(word)) {
                longFormStopWords.add(word);
                numLongFormStopwordChars += word.length();
                if (currentWordMatches) {
                    numMatchedStopwordChars += numCurrentWordMatchedChars;
                }
            } else {
                // word is not a stop word
                longFormWordsExcludingStopwords.add(word);
                if (currentWordMatches) {
                    // current word matches and not a stopword
                    matchedWordsExcludingStopwords.add(word);
                }
            }

        }


        float wordMatchRatio = getWordMatchRatio(matchedWordsExcludingStopwords, longFormWordsExcludingStopwords);
        float charMatchRatio = getCharMatchRatio(shortForm, numMatchedStopwordChars, matchedWordsExcludingStopwords.size(),
                longForm, numLongFormStopwordChars, longFormWordsExcludingStopwords.size());

        float score = computeScore(wordMatchRatio, charMatchRatio);
        alignment.setScore(score);
    }


    float getWordMatchRatio(List<String> matchedWords, List<String> longFormWords) {
        Map<String, Float> tfIdfScoreMap = tfIdfTermScorer.getScoreMap(longFormWords);

        float longFormCumulativeScore = 0f;
        float matchedWordsCumulativeScore = 0f;
        try {

            for (String word : longFormWords) {
                longFormCumulativeScore += tfIdfScoreMap.get(word);
            }

            for (String word : matchedWords) {
                matchedWordsCumulativeScore += tfIdfScoreMap.get(word);
            }
        } catch (Exception e) {
            System.out.println(e.getCause());
        }

        float wordMatchRatio = longFormCumulativeScore == 0 ? 0 : matchedWordsCumulativeScore / longFormCumulativeScore;
        return wordMatchRatio;
    }

    float getCharMatchRatio(String shortForm, int numMatchedStopwordChars, int numMatchedWordsExcludingStopWords,
                            String longForm, int numLongFormStopwordChars, int numLongFormWordsExcludingStopWords) {


        float finalMatchedChars = shortForm.length() - numMatchedStopwordChars - numMatchedWordsExcludingStopWords;
        float finalLongFormChars = longForm.length() - numLongFormStopwordChars - numLongFormWordsExcludingStopWords;

        float charMatchRatio = finalLongFormChars == 0 ? 0 : finalMatchedChars / finalLongFormChars;
        return charMatchRatio;
    }

    float computeScore(float wordMatchRatio, float charMatchRatio) {
        return 90 * wordMatchRatio + 10 * charMatchRatio;
    }
}
