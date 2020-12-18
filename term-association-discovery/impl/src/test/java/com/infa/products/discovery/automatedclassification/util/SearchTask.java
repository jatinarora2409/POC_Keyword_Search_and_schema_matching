package com.infa.products.discovery.automatedclassification.util;

import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.SearchTaskException;
import com.infa.products.discovery.automatedclassification.model.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class SearchTask implements Runnable {
    private String asset;
    private Searcher<BusinessTerm> searcher;
    private String correctTerm;
    private AtomicLong correctMatch;
    private AtomicLong incorrectMatch;
    private AtomicLong correctRecommendation;
    private AtomicLong incorrectRecommendation;
    private AtomicLong noMatch;

    public SearchTask(String asset, Searcher<BusinessTerm> searcher, String correctTerm, AtomicLong correctMatch, AtomicLong incorrectMatch, AtomicLong correctRecommendation, AtomicLong incorrectRecommendation, AtomicLong noMatch) {
        this.asset = asset;
        this.searcher = searcher;
        this.correctTerm = correctTerm;
        this.correctMatch = correctMatch;
        this.incorrectMatch = incorrectMatch;
        this.correctRecommendation = correctRecommendation;
        this.incorrectRecommendation = incorrectRecommendation;
        this.noMatch = noMatch;
    }

    public void run() {
        try {
            //Query query = new Query(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), asset);
            Asset asset1 = new Asset("", asset, AssetType.DATA_ELEMENT);
            List<SearchResult<BusinessTerm>> results = searcher.search(asset1, 10);
            if (!results.isEmpty()) {
                SearchResult<BusinessTerm> topResult = results.get(0);
                if (topResult.getSimilarityScore() >= 90.0f) {
                    if (correctTerm.equalsIgnoreCase(topResult.getDocument().getName())) {
                        correctMatch.getAndIncrement();
                    } else {
//                        System.out.println(asset + "[" + correctTerm + "]" + " : " + topResult.getDocument().getName() + " [" + topResult.getSimilarityScore() + "]");
                        incorrectMatch.getAndIncrement();
//                        System.out.println(asset+" "+correctTerm+" "+topResult.getDocument().getName());
                    }
                } else {
                    if (correctTerm.equalsIgnoreCase(topResult.getDocument().getName())) {
//                       System.out.println(asset+" "+correctTerm);
                        correctRecommendation.getAndIncrement();
                    } else {
                        incorrectRecommendation.getAndIncrement();
                    }
                }
            } else {
                System.out.println(asset+" "+correctTerm);
                noMatch.getAndIncrement();
            }
        } catch (DocumentSearchException e) {
            throw new SearchTaskException(e);
        }
    }
}
