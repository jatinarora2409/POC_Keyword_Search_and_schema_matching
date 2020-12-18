package com.infa.products.discovery.automatedclassification.util;


import org.apache.lucene.util.automaton.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.infa.products.discovery.automatedclassification.util.DiscoveryConstants.*;

/**
 * @author halshi
 */
public final class TermAssociationHelper {

    static private Logger LOGGER = LoggerFactory.getLogger(TermAssociationHelper.class);

    private static volatile Set<String> dbeSet;

    private static volatile Set<String> resourceNames;



//    public static String getTableId(String columnId) {
//        return columnId.substring(0, columnId.lastIndexOf('/'));
//    }



//    public static void validateObjects(Object... args) {
//
//        for (Object arg : args) {
//            Objects.requireNonNull(arg);
//        }
//    }
//
//    public static void validateNotEmpty(String[] args) {
//
//        for (String arg : args) {
//            if (arg.isEmpty()) {
//                throw new IllegalArgumentException("arg cannot be empty.");
//            }
//        }
//    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static <T> Collection<T> safe(Collection<T> collection) {
        return collection == null ? Collections.<T>emptyList() : collection;
    }

    public static <T> boolean isEmptyList(Collection<T> collection) {
        return ((collection == null) || collection.isEmpty()) ? true : false;
    }


//    public static int getBatchSize() {
//        String batchSize = System.getProperty(BATCH_SIZE_OPTION, DEFAULT_ASSETS_TO_PROCESS_BATCH_SIZE);
//        return getIntValue(batchSize, Integer.valueOf(DEFAULT_ASSETS_TO_PROCESS_BATCH_SIZE));
//    }
//
//    public static int getNumOfSearchThreads() {
//        String numOfSearchThreads = System.getProperty(SEARCH_THREADS_COUNT_OPTION, DEFAULT_SEARCH_THREADS_COUNT);
//        return getIntValue(numOfSearchThreads, Integer.valueOf(DEFAULT_SEARCH_THREADS_COUNT));
//    }

    public static int getCutOffScore() {
        String cutOffScore = System.getProperty(CUT_OFF_SCORE_OPTION, DEFAULT_CUT_OFF_SCORE);
        return getIntValue(cutOffScore, Integer.valueOf(DEFAULT_CUT_OFF_SCORE));
    }

//    public static int getMaxCandidatesConsidered() {
//        String maxCandidatesConsidered = System.getProperty(MAX_CANDIDATES_CONSIDERED_OPTION, DEFAULT_MAX_CANDIDATES_CONSIDERED);
//        return getIntValue(maxCandidatesConsidered, Integer.valueOf(DEFAULT_MAX_CANDIDATES_CONSIDERED));
//    }
//
//
//    public static int getMaxResultsAccepted() {
//        String maxCandidatesConsidered = System.getProperty(MAX_RESULTS_ACCEPTED_OPTION, DEFAULT_MAX_RESULTS_ACCEPTED);
//        return getIntValue(maxCandidatesConsidered, Integer.valueOf(DEFAULT_MAX_RESULTS_ACCEPTED));
//    }

    public static int getMaxDeterminizedStates() {
        String maxDeterminizedStates = System.getProperty(MAX_DETERMINIZED_STATES_OPTION);
        return getIntValue(maxDeterminizedStates, Operations.DEFAULT_MAX_DETERMINIZED_STATES);
    }

    public static int getCharMatchScore() {
        String charMatchScore = System.getProperty(CHAR_MATCH_SCORE_OPTION, DEFAULT_CHAR_MATCH_SCORE);
        return getIntValue(charMatchScore, Integer.valueOf(DEFAULT_CHAR_MATCH_SCORE));
    }


    public static int getStopWordMatchBoost() {
        String stopWordMatchBoost = System.getProperty(STOP_WORD_MATCH_BOOST_OPTION, DEFAULT_STOP_WORD_MATCH_BOOST);
        return getIntValue(stopWordMatchBoost, Integer.valueOf(DEFAULT_STOP_WORD_MATCH_BOOST));
    }

    public static int getWordMatchBoost() {
        String wordMatchBoost = System.getProperty(WORD_MATCH_BOOST_OPTION, DEFAULT_WORD_MATCH_BOOST);
        return getIntValue(wordMatchBoost, Integer.valueOf(DEFAULT_WORD_MATCH_BOOST));
    }

    private static int getIntValue(String strValue, int defaultValue) {
        try {
            return Integer.valueOf(strValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


}
