package com.infa.products.discovery.automatedclassification.util;

import com.infa.products.discovery.automatedclassification.model.BusinessTerm;
import com.infa.products.discovery.automatedclassification.model.SearchResult;
import com.infa.products.discovery.automatedclassification.model.TermType;

import java.util.*;
import java.util.stream.Collectors;

public final class Utils {

    private Utils() {
        // Preventing instantiation
    }

    public static BusinessTerm createBusinessTerm(final String catalogId, final String name,
                                                  final Set<String> synonyms, final String type) {
        return BusinessTerm.builder().withCatalogId(catalogId).withName(name)
                .withSynonyms(synonyms).withType(TermType.valueOf(type)).build();
    }

    public static BusinessTerm createBusinessTerm(final String catalogId, final String name, final String type) {
        return createBusinessTerm(catalogId, name, null, type);
    }

    public static void main(String[] args) {
        Utils.createBusinessTerm("BT1", "Metric Name", "BG");
    }

    public static List<SearchResult<BusinessTerm>> merge(final List<SearchResult<BusinessTerm>> searchResults1,
                                                         final List<SearchResult<BusinessTerm>> searchResults2, int maxResultsAccepted) {
        final Map<String, SearchResult<BusinessTerm>> resultsMap = new HashMap<>();
        for (final SearchResult<BusinessTerm> searchResult : searchResults1) {
            resultsMap.put(searchResult.getDocument().getId(), searchResult);
        }
        for (final SearchResult<BusinessTerm> searchResult : searchResults2) {
            final String id = searchResult.getDocument().getId();
            if (resultsMap.containsKey(id)) {
                final SearchResult<BusinessTerm> existingSearchResult = resultsMap.get(id);
                final float existingSimScore = existingSearchResult.getSimilarityScore();
                final float simScore = searchResult.getSimilarityScore();
                if (existingSimScore < simScore) {
                    resultsMap.put(id, searchResult);
                }
            } else {
                resultsMap.put(id, searchResult);
            }
        }
        final List<SearchResult<BusinessTerm>> list = new LinkedList<SearchResult<BusinessTerm>>(resultsMap.values());
        Collections.sort(list, new Comparator<SearchResult<BusinessTerm>>() {

            @Override
            public int compare(final SearchResult<BusinessTerm> o1, final SearchResult<BusinessTerm> o2) {
                final SearchResult<BusinessTerm> res1 = (SearchResult<BusinessTerm>) o1;
                final SearchResult<BusinessTerm> res2 = (SearchResult<BusinessTerm>) o2;
                final float score1 = res1.getSimilarityScore();
                final float score2 = res2.getSimilarityScore();
                return (int) (score2 - score1);
            }
        });

        return Collections.unmodifiableList(list.stream().limit(maxResultsAccepted).collect(Collectors.toList()));
    }
}
