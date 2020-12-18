package com.infa.products.discovery.automatedclassification.util;



import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigUtil {
    //below set is a thread safe set
    private static final Set<String> ENGLISH_STOP_WORD_SET = ConcurrentHashMap.newKeySet();


    private ConfigUtil() {

    }



    //This method is used only for running unit testcases
    public static void setEnglishStopWordSet(List<String> englishStopWordSet) {
        ENGLISH_STOP_WORD_SET.addAll(englishStopWordSet);
    }

    public static Set<String> getEnglishStopWordSet() {
        return Collections.unmodifiableSet(ENGLISH_STOP_WORD_SET);
    }


    public static String BG = "BG";

    private static int toInt(String object) {
        return Integer.parseInt(object);
    }

    private static float toFloat(String object) {
        return Float.parseFloat(object);
    }



}
