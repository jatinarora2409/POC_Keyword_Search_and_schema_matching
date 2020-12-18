package com.infa.products.discovery.automatedclassification.engine.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  This class is responsible for TfIdf scoring of terms
 *
 *  TfIdf(term) = term-frequency(term) * idf(term)
 *
 *  Normalised-TfIdf(term) = TfIdf(term) / sqrt( sum( TfIdf(term)^2, for all terms) )
 *
 * @author vshivam
 */
public class TfIdfTermScorer {

    private final Map<String, Float> idfMap;

    private static final Logger LOGGER = LoggerFactory.getLogger(TfIdfTermScorer.class);

    public TfIdfTermScorer(Map<String, Float> idfMap) {
        this.idfMap = idfMap;
    }

    /**
     * @param terms : list of terms
     * @return : terms to tfIdf score map of provided term list
     */
    public Map<String, Float> getScoreMap(List<String> terms) {
        Map<String, Float> tfIdfScoreMap = new HashMap<>();

        // term frequency map
        Map<String, Integer> tfMap = new HashMap<>();
        for (String term : terms) {
            Integer termFrequency = tfMap.getOrDefault(term, 0);
            tfMap.put(term, termFrequency + 1);
        }

        Float normalizingDenominator = 0f;
        for (String term : tfMap.keySet()) {
            // check IDF cache
            if(idfMap.containsKey(term)) {
                Float tfIdfScore = tfMap.get(term) * idfMap.get(term);
                tfIdfScoreMap.put(term, tfIdfScore);
                normalizingDenominator += tfIdfScore * tfIdfScore;
            }

            else {
                LOGGER.warn("Term: '" +  term + "' not present in index");
            }
        }
        normalizingDenominator = (float)Math.sqrt(normalizingDenominator);

        // normalize TfIdf score
        for (String term: tfIdfScoreMap.keySet()) {
            Float tfIdfScore = tfIdfScoreMap.get(term);
            Float normaizedTfIdfScore = tfIdfScore/normalizingDenominator;
            tfIdfScoreMap.put(term, normaizedTfIdfScore);
        }

        return tfIdfScoreMap;
    }
}
