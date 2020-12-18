package com.infa.products.discovery.automatedclassification.similarity;

import com.infa.products.discovery.automatedclassification.engine.search.TfIdfTermScorer;
import com.infa.products.discovery.automatedclassification.exception.InvalidAlignmentCandidatesException;
import com.infa.products.discovery.automatedclassification.similarity.api.Alignment;
import com.infa.products.discovery.automatedclassification.similarity.api.AlignmentComputer;

import java.util.*;

public class BagOfWordsAlignmentComputer implements AlignmentComputer {

    private final Set<String> stopWordSet;
    List<String> longFormWords;
    List<String> shortFormWords;
    List<String> matchedStopWords;
    private final TfIdfTermScorer tfIdfTermScorer;
    private List<String> matchedWords;
    private float bagOfWordsScore;
    private int numCharactersMatched;
    private int numStopwordsMatched;
    private int numStopwordCharsMatched;

    static final int MAX_STRING_SIZE = 255;

    public BagOfWordsAlignmentComputer(Set<String> stopWordSet, TfIdfTermScorer tfIdfTermScorer) {
        this.stopWordSet = stopWordSet;
        this.tfIdfTermScorer = tfIdfTermScorer;
    }

    List<String> removeStopWords(List<String> words) {
        List<String> stopWordRemovedWords = new ArrayList<>();
        for (String word: words) {
            if(!stopWordSet.contains(word)) {
                stopWordRemovedWords.add(word);
            }
        }
        return stopWordRemovedWords;
    }

    private float getWordMatchRatio(List<String> matchedWords, List<String> longFormWords) {
        Map<String, Float> tfidfScoreMap = tfIdfTermScorer.getScoreMap(longFormWords);

        float matchedWordsScore = 0f;
        for (String word: matchedWords) {
            matchedWordsScore += tfidfScoreMap.get(word);
        }

        float longFormWordsScore = 0f;
        for (String word: longFormWords) {
            longFormWordsScore += tfidfScoreMap.get(word);
        }

        float wordMatchRatio = matchedWordsScore/longFormWordsScore;
        return wordMatchRatio;
    }

    private float getCharMatchRatio(List<String> matchedWords, List<String> longFormWords) {
        int numMatchedChars = 0;
        for (String word: matchedWords) {
            numMatchedChars += word.length();
        }
        this.numCharactersMatched = numMatchedChars;

        int numLongFormChars = 0;
        for (String word: longFormWords) {
            numLongFormChars += word.length();
        }

        int numMatchedWords = matchedWords.size();
        int numLongFormWords = longFormWords.size();

        float numMatchedCharsOtherThanFirstLetters =  numMatchedChars - numMatchedWords;
        float numLongFormCharsOtherThanFirstLetters = numLongFormChars - numLongFormWords;

        float charMatchRatio = numLongFormCharsOtherThanFirstLetters == 0 ? 0 : (numMatchedCharsOtherThanFirstLetters / numLongFormCharsOtherThanFirstLetters);
        return charMatchRatio;
    }

    private float computeScore(List<String> matchedWords, List<String> longFormWords) {
        float wordMatchRatio = getWordMatchRatio(matchedWords, longFormWords);
        float charMatchRatio = getCharMatchRatio(matchedWords, longFormWords);

        return 90*wordMatchRatio + 10*charMatchRatio;
    }

    private int getNumStopwordCharsMatched() {
        int numStopwordCharmatched = 0;
        for (String word: matchedStopWords) {
            numStopwordCharmatched += word.length();
        }
        return numStopwordCharmatched;
    }

    private List<String> getMatchedStopwords() {
        List<String> matchedStopwords = new ArrayList<>();
        for (String word: shortFormWords) {
            if(stopWordSet.contains(word)) {
                matchedStopwords.add(word);
            }
        }
        return matchedStopwords;
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

    private boolean isSubset(List<String> shortFormWords, List<String> longFormWords) {
        Map<String, Integer> longFormFreqMap = new HashMap<>();
        for (String word: longFormWords) {
            int freq = longFormFreqMap.getOrDefault(word, 0);
            longFormFreqMap.put(word, freq+1);
        }

        for (String word: shortFormWords) {
            int freq = longFormFreqMap.getOrDefault(word, 0);
            if(freq == 0) {
                return false;
            }
            else {
                longFormFreqMap.put(word, freq-1);
            }
        }
        return true;
    }


    @Override
    synchronized public Alignment compute(String longForm, String shortForm) throws InvalidAlignmentCandidatesException {

        // validateInputStrings
        validateInputString(longForm);
        validateInputString(shortForm);

        // TODO: not necessary (in case of multiword synonym this asumption can be wrong)
//        if (shortForm.length() > longForm.length()) {
//            throw new InvalidAlignmentCandidatesException(
//                    "Short form longer than long form [LF :(" + longForm + ") SF: (" + shortForm + ")]", longForm,
//                    shortForm);
//        }

        this.longFormWords = Arrays.asList(longForm.split("\\s+"));
        this.shortFormWords = Arrays.asList(shortForm.split("\\s+"));

        // check if shortForm words are subset of longForm words.
        if (!isSubset(shortFormWords, longFormWords)) {
            return null;
        }

        this.matchedStopWords = getMatchedStopwords();

        // remove stopwords
        this.longFormWords = removeStopWords(longFormWords);
        this.shortFormWords = removeStopWords(shortFormWords);

        // matchedWords are same as shortform words with stopwords removed
        this.matchedWords = this.shortFormWords;
        this.numStopwordsMatched = this.matchedStopWords.size();
        this.numStopwordCharsMatched = getNumStopwordCharsMatched();

        this.bagOfWordsScore = computeScore(matchedWords, longFormWords);



        Alignment alignment = new Alignment() {
            @Override
            public String getLongForm() {
                return longForm;
            }

            @Override
            public String getShortForm() {
                return shortForm;
            }

            @Override
            public int getNumWordsMatched() {
                return matchedWords.size();
            }

            @Override
            public int getNumCharactersMatched() {
                return numCharactersMatched;
            }

            @Override
            public int getNumStopwordsMatched() {
                return numStopwordsMatched;
            }

            @Override
            public int getNumStopwordCharsMatched() {
                return numStopwordCharsMatched;
            }

            @Override
            public AlignmentResult resultAt(int index) {
                return null;
            }

            @Override
            public float getScore() {
                return bagOfWordsScore;
            }

            @Override
            public void setScore(float score) {
                bagOfWordsScore = score;
            }

            @Override
            public boolean equals(String alignedShortForm) {
                return false;
            }

        };
        return alignment;
    }
}
