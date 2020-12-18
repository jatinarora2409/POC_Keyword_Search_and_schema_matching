package com.infa.products.discovery.automatedclassification.engine.search;

import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.InvalidAlignmentCandidatesException;
import com.infa.products.discovery.automatedclassification.model.*;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;
import com.infa.products.discovery.automatedclassification.similarity.AbbreviationAlignmentScorer;
import com.infa.products.discovery.automatedclassification.similarity.AlignmentComputerFactory;
import com.infa.products.discovery.automatedclassification.similarity.BagOfWordsAlignmentComputer;
import com.infa.products.discovery.automatedclassification.similarity.api.Alignment;
import com.infa.products.discovery.automatedclassification.similarity.api.AlignmentComputer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.index.IndexReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.infa.products.discovery.automatedclassification.util.DiscoveryConstants.SPACE;
import static com.infa.products.discovery.automatedclassification.util.DiscoveryConstants.UNDERSCORE;
import static com.infa.products.discovery.automatedclassification.util.TermAssociationHelper.getCutOffScore;

/**
 * This searcher ranks the results from multiple searching algorithms and returns the eligible candidates.
 *
 * @author vshivam
 */
public class BusinessTermSearcher implements Searcher<BusinessTerm> {

    private static final int MAX_ALIGNMENT_CANDIDATES = 500;
    private static final int MAX_BAG_OF_WORDS_CANDIDATES = 500;

    private static final int CUT_OFF_SCORE = getCutOffScore();

    private final DocumentType<BusinessTerm> docType = new BusinessTermType();

    private final Set<String> stopWordSet = new HashSet<>();
    private final List<String> ignoredPrefixes = new ArrayList<>();

    private final Searcher<BusinessTerm> alignmentSearcher;
    private final Searcher<BusinessTerm> bagOfWordsSearcher;

    private final TfIdfTermScorer tfIdfTermScorer;

    private final Logger LOGGER = LoggerFactory.getLogger(BusinessTermSearcher.class);

    // TODO: check for synchronization issues.
    public BusinessTermSearcher(IndexReader reader, List<String> stopWordList, List<String> ignoredPrefixList, SynonymMap synonymMap) throws IOException {
        if (!Objects.isNull(stopWordList)) {
            for (String stopword : stopWordList) {
                this.stopWordSet.add(stopword.toLowerCase().trim());
            }
        }
        if (!Objects.isNull(ignoredPrefixList)) {
            for (String prefix : ignoredPrefixList) {
                this.ignoredPrefixes.add(prefix.toLowerCase().trim());
            }
        }

        LOGGER.info("Building TfIdfTermScorer...");
        long startTime = System.currentTimeMillis();

        IdfMapBuilder idfMapBuilder = new IdfMapBuilder(reader);
        tfIdfTermScorer = new TfIdfTermScorer(idfMapBuilder.getTfIdfMap());

        long endTime = System.currentTimeMillis();
        LOGGER.info("TfIdfTermScorer build completion time:::" + (endTime - startTime) + " in ms.");

        alignmentSearcher = new AlignmentSearcher(reader, new AlignmentQueryAnalyzer(synonymMap), docType);
        bagOfWordsSearcher = new BagOfWordsSearcher(reader, new BagOfWordsQueryAnalyzer(synonymMap), docType);
    }

    @Override
    public List<SearchResult<BusinessTerm>> search(Asset asset, int maxResults) throws DocumentSearchException {
        List<SearchResult<BusinessTerm>> results = new LinkedList<>();
        asset = getProcessedAsset(asset);

        if (asset.getName().isEmpty()) {
            // return empty result set
            return results;
        }

        results.addAll(getAlignmentCandidates(asset, MAX_ALIGNMENT_CANDIDATES));
//        results.addAll(getBagOfWordsCandidates(asset, MAX_BAG_OF_WORDS_CANDIDATES));

        return getTopResults(results, maxResults);
    }

    private Asset getProcessedAsset(Asset asset) {
        String assetName = asset.getName();
        String modifiedName = assetName.toLowerCase();
        modifiedName = removePrefixes(modifiedName);
        modifiedName = modifiedName.replaceAll(UNDERSCORE, SPACE);
        modifiedName = processSpecialChars(modifiedName);
        if(asset.getParent().isPresent()) {
            return new Asset(asset.getCatalogId(), modifiedName, asset.getAssetType(), asset.getParent().get());
        }
        return new Asset(asset.getCatalogId(), modifiedName, asset.getAssetType());
    }

    private String removePrefixes(String queryText) {
        for (String prefix : ignoredPrefixes) {
            if (queryText.startsWith(prefix)) {
                queryText = queryText.substring(prefix.length());
                break;
            }
        }
        return queryText.trim();
    }

    List<SearchResult<BusinessTerm>> getTopResults(List<SearchResult<BusinessTerm>> searchResults, int maxResults) {
        searchResults.sort((arg0, arg1) -> Float.compare(arg1.getSimilarityScore(), arg0.getSimilarityScore()));
        filterDuplicateSearchResults(searchResults);
        if (searchResults.size() > maxResults) {
            return searchResults.subList(0, maxResults);
        } else {
            return searchResults;
        }

    }

    private void filterDuplicateSearchResults(List<SearchResult<BusinessTerm>> searchResults) {
        Set<BusinessTerm> docs = new HashSet<>();
        Iterator<SearchResult<BusinessTerm>> searchResultIterator = searchResults.iterator();
        while (searchResultIterator.hasNext()) {
            SearchResult<BusinessTerm> searchResult = searchResultIterator.next();
            BusinessTerm doc = searchResult.getDocument();
            if (docs.contains(doc)) {
                searchResultIterator.remove();
            } else {
                docs.add(doc);
            }
        }
    }


    /**
     * Returns scored Alignment Candidates
     */
    List<SearchResult<BusinessTerm>> getAlignmentCandidates(Asset asset, int maxCandidates) throws DocumentSearchException {
        List<SearchResult<BusinessTerm>> alignmentCandidates = alignmentSearcher.search(asset, maxCandidates);
        // search for table context assets.
        for (Asset tableContextAsset : getAssetsWithTableContext(asset)) {
            alignmentCandidates.addAll(alignmentSearcher.search(tableContextAsset, maxCandidates));
        }

        List<SearchResult<BusinessTerm>> results = new ArrayList<>();
        // TODO: Implement caching

        // perform alignment scoring
        for (SearchResult<BusinessTerm> candidate : alignmentCandidates) {
            final String capturedString = candidate.getQuery().getQueryText();  // regex query string
            String matchedString = candidate.getMatchedString();  // String in document which was returned as result.
            float score = computeAlignmentScore(matchedString, capturedString);
            if (score > CUT_OFF_SCORE) {
                results.add(SearchResult.<BusinessTerm>builder().withQuery(candidate.getQuery())
                        .withDocument(candidate.getDocument()).withMatchedString(candidate.getMatchedString()).withSimilarityScore(score).build());
            }
        }
        return results;
    }

    private float computeAlignmentScore(String matchedString, String capturedString) {
        matchedString = processSpecialChars(matchedString);
        capturedString = processSpecialChars(capturedString);
        LOGGER.debug("Alignment_matchedString: " + matchedString);
        LOGGER.debug("Alignment_capturedString: " + capturedString);
        try {
            AlignmentComputer alignmentComputer = AlignmentComputerFactory.getAlignmentComputer(new AbbreviationAlignmentScorer(stopWordSet, tfIdfTermScorer));
            final Alignment alignment = alignmentComputer.compute(matchedString, capturedString);
            return Objects.isNull(alignment) ? -1 : alignment.getScore();
        } catch (InvalidAlignmentCandidatesException e) {
            e.printStackTrace();
            return -1f;
        }
    }


    /**
     * Returns scored Bag-of-Words candidates
     */
    List<SearchResult<BusinessTerm>> getBagOfWordsCandidates(Asset asset, int maxCandidates) throws DocumentSearchException {
        List<SearchResult<BusinessTerm>> bagOfWordsCandidates = bagOfWordsSearcher.search(asset, maxCandidates);
        // search for table context asset
        List<Asset> tableContextAssets = getAssetsWithTableContext(asset);
        if (!tableContextAssets.isEmpty()) {
            // For bag of words, all table contexts asset are equivalent, thus we need searching for only one of them.
            bagOfWordsCandidates.addAll(bagOfWordsSearcher.search(tableContextAssets.get(0), maxCandidates));
        }

        List<SearchResult<BusinessTerm>> results = new ArrayList<>();

        // perform bag-of-words scoring
        for (SearchResult<BusinessTerm> candidate : bagOfWordsCandidates) {
            final String capturedString = candidate.getQuery().getQueryText();
            String matchedString = candidate.getMatchedString();
            float score = computeBagOfWordsScore(matchedString, capturedString);

            if (score > CUT_OFF_SCORE) {
                results.add(SearchResult.<BusinessTerm>builder().withQuery(candidate.getQuery())
                        .withDocument(candidate.getDocument()).withMatchedString(candidate.getMatchedString()).withSimilarityScore(score).build());
            }

        }
        return results;
    }

    private float computeBagOfWordsScore(String matchedString, String capturedString) {
        matchedString = processSpecialChars(matchedString);
        capturedString = processSpecialChars(capturedString);
        LOGGER.debug("BOF_matchedString: " + matchedString);
        LOGGER.debug("BOF_capturedString: " + capturedString);
        try {
            AlignmentComputer alignmentComputer = new BagOfWordsAlignmentComputer(stopWordSet, tfIdfTermScorer);
            Alignment alignment = alignmentComputer.compute(matchedString, capturedString);
            return Objects.isNull(alignment) ? -1 : alignment.getScore();
        } catch (InvalidAlignmentCandidatesException e) {
            e.printStackTrace();
            return -1f;
        }
    }


    public static String processSpecialChars(String matchedString) {
        matchedString = matchedString.replaceAll("[^A-Za-z0-9%']", " ");
        matchedString = matchedString.replaceAll("[']", "");
        matchedString = matchedString.replaceAll("\\s+", " ").trim();
        return matchedString.toLowerCase();
    }

    List<Asset> getAssetsWithTableContext(Asset asset) {
        List<Asset> assets = new ArrayList<>();

        String catalogID = asset.getCatalogId();
        AssetType assetType = asset.getAssetType();
        String assetName = asset.getName();

        if (asset.getParent().isPresent()) {

            String parent = asset.getParent().get();
            String processedParentName = processSpecialChars(parent);

            // prepend table name
            String prependTableName = processedParentName + " " + assetName;
            assets.add(new Asset(catalogID, prependTableName, assetType, parent));

            // append table name
            String appendTableName = assetName + " " + processedParentName;
            assets.add(new Asset(catalogID, appendTableName, assetType, parent));
        }
        return assets;
    }

    @Override
    public void close() throws IOException {
        if (alignmentSearcher != null)
            alignmentSearcher.close();

        if (bagOfWordsSearcher != null)
            bagOfWordsSearcher.close();

    }
}
