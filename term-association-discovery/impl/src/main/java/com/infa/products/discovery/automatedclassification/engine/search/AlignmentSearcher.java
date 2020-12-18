package com.infa.products.discovery.automatedclassification.engine.search;

import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.model.*;
import com.infa.products.discovery.automatedclassification.model.api.DocumentBuilder;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;
import com.infa.products.discovery.automatedclassification.util.Pair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.automaton.RegExp;
import org.apache.lucene.util.automaton.TooComplexToDeterminizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.infa.products.discovery.automatedclassification.util.DiscoveryConstants.*;
import static com.infa.products.discovery.automatedclassification.util.TermAssociationHelper.getMaxDeterminizedStates;

public class AlignmentSearcher implements Searcher<BusinessTerm> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlignmentSearcher.class);
    private static AtomicLong totalNumOfQueriesSearched = new AtomicLong(0);
    private static AtomicLong numOfTimesQueriesSplit = new AtomicLong(0);
    private static final int MAX_DETERMINIZED_STATES = getMaxDeterminizedStates();
    private final IndexSearcher searcher;

    private final Analyzer queryAnalyzer;

    private final DocumentType<BusinessTerm> docType;

    public AlignmentSearcher(IndexReader reader, Analyzer queryAnalyzer, DocumentType<BusinessTerm> docType) {
        this.searcher = new IndexSearcher(reader);
        this.queryAnalyzer = queryAnalyzer;
        this.docType = docType;
    }

    @Override
    public List<SearchResult<BusinessTerm>> search(Asset asset, int numHits) throws DocumentSearchException {
        String indexedField = BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName();
        String queryText = asset.getName();

        List<SearchResult<BusinessTerm>> searchResults;
        TokenStream stream = getTokenStream(indexedField, queryText);
        AlignmentRegex regex;
        try {
            regex = AlignmentQueryBuilder.getRegex(indexedField, stream);
            stream.close();
        } catch (IOException e) {
            throw new DocumentSearchException("Error building candidate search regular expression", e);
        }
        Map<Integer, List<Pair<String, String>>> idCSRegexMap = regex.getRegexQueries();
        List<Result> results;
        try {
            String regexQuery = getAsSingleRegex(idCSRegexMap);
            results = Arrays.asList(search(indexedField, regexQuery, numHits));
        } catch (TooComplexToDeterminizeException e) {
            //If TooComplexToDeterminizeException occurs for a single Big Query then it is split into multiple small queries and tried again
            numOfTimesQueriesSplit.getAndIncrement();
            results = search(indexedField, idCSRegexMap, numHits);
        } catch (IOException e) {
            throw new DocumentSearchException("Regular expression query failed.", e);
        }
        searchResults = processResults(results, numHits, indexedField, regex);
        return searchResults;
    }

    private List<SearchResult<BusinessTerm>> processResults(List<Result> results, int numHits, String indexedField, AlignmentRegex regex) {
        List<SearchResult<BusinessTerm>> searchResults = new LinkedList<>();
        for (Result result : results) {
            TopDocs td = result.topDocs;
            ScoreDoc[] hits = td.scoreDocs;
            if (td.totalHits == 0) {
                continue;
            }
            long end = Math.min(td.totalHits, numHits);
            for (int j = 0; j < end; j++) { // Per hit i.e. per business term
                Document d;
                try {
                    d = searcher.doc(hits[j].doc);
                    final BusinessTerm doc = getDoc(d);
                    final String[] termNames = d.getValues(indexedField);
                    for (final String name : termNames) {
                        String capturedString;
                        if (regex.checkMatch(name.toLowerCase(), result.regexQuery)) {
                            capturedString = getCapturedString(regex, result, name);
                        } else {
                            continue;
                        }
                        final Query query = new Query(indexedField, capturedString);
                        final SearchResult<BusinessTerm> searchResult = SearchResult.<BusinessTerm>builder()
                                .withQuery(query).withDocument(doc).withMatchedString(name).build();
                        searchResults.add(searchResult);
                    }
                } catch (IOException e) {
                    LOGGER.error("Error retrieving document." + e);
                }
            }
        }
        return searchResults;
    }

    private BusinessTerm getDoc(final Document d) {
        DocumentBuilder<BusinessTerm> docBuilder = docType.getDocumentBuilder();
        for (Field f : docType.getFields()) {
            docBuilder.withField(f.getFieldName(), d.getValues(f.getFieldName()));
        }
        return docBuilder.build();
    }

    private String getCapturedString(AlignmentRegex alignmentRegex, Result result, String matchedString) {
        if (result.capturedString.isEmpty()) {
            return alignmentRegex.getCapturedString(matchedString.toLowerCase(), result.regexQuery);
        }
        return result.capturedString;
    }

    private Result search(String indexedField, String regexQuery, int numHits) throws IOException, TooComplexToDeterminizeException {
        LOGGER.debug("RegexQuery:" + regexQuery);
        Term term = new Term(indexedField, regexQuery);
        org.apache.lucene.search.Query luceneQuery = new RegexpQuery(term, RegExp.ALL, MAX_DETERMINIZED_STATES);
        TopDocs td = searcher.search(luceneQuery, numHits);
        totalNumOfQueriesSearched.getAndIncrement();
        Result result = new Result(regexQuery, "", td);
        return result;
    }

    private List<Result> search(String indexedField, Map<Integer, List<Pair<String, String>>> idCSRegexMap, int numHits) {
        List<Result> results = new LinkedList<>();
        List<Pair<String, String>> csRegexQueryPairs = new LinkedList<>();
        createSplitRegexQueriesAndCapturedStrings(1, idCSRegexMap, new StringBuilder(), new StringBuilder(), csRegexQueryPairs);
        for (Pair<String, String> csRegexQueryPair : csRegexQueryPairs) {
            String regexQuery = csRegexQueryPair.getSecond();
            try {
                Result result = search(indexedField, regexQuery, numHits);
                result.capturedString = csRegexQueryPair.getFirst();
                results.add(result);
            } catch (TooComplexToDeterminizeException e) {
                LOGGER.error("TooComplexToDeterminizeException occurred for regex query:" + regexQuery);
            } catch (IOException e) {
                LOGGER.error("IOException occurred for regex query:" + regexQuery);
            }
        }
        return results;
    }

    private void createSplitRegexQueriesAndCapturedStrings(int sequenceId, Map<Integer, List<Pair<String, String>>> idCSRegexMap, StringBuilder regexStringBuilder, StringBuilder capturedStringBuilder, List<Pair<String, String>> csRegexQueryPairs) {
        String regexCurrentState = regexStringBuilder.toString();
        String csCurrentState = capturedStringBuilder.toString();
        if (!idCSRegexMap.containsKey(sequenceId)) {
            Pair<String, String> csRegexQueryPair = new Pair<>(capturedStringBuilder.toString().trim(), regexStringBuilder.toString());
            csRegexQueryPairs.add(csRegexQueryPair);
            return;
        }
        List<Pair<String, String>> capturedStringRegexList = idCSRegexMap.get(sequenceId);
        for (int i = 0; i < capturedStringRegexList.size(); i++) {
            if (i == 0) {
                capturedStringBuilder = capturedStringBuilder.append(capturedStringRegexList.get(i).getFirst()).append(SPACE);
                regexStringBuilder = regexStringBuilder.append(capturedStringRegexList.get(i).getSecond());
            } else if (i > 0) {
                int csStartIndex = capturedStringBuilder.lastIndexOf(capturedStringRegexList.get(i - 1).getFirst());
                int csEndIndex = csStartIndex + capturedStringRegexList.get(i - 1).getFirst().length();
                capturedStringBuilder.replace(csStartIndex, csEndIndex, capturedStringRegexList.get(i).getFirst());

                int regexStartIndex = regexStringBuilder.lastIndexOf(capturedStringRegexList.get(i - 1).getSecond());
                int regexEndIndex = regexStartIndex + capturedStringRegexList.get(i - 1).getSecond().length();
                regexStringBuilder.replace(regexStartIndex, regexEndIndex, capturedStringRegexList.get(i).getSecond());

            }
            createSplitRegexQueriesAndCapturedStrings(sequenceId + 1, idCSRegexMap, regexStringBuilder, capturedStringBuilder, csRegexQueryPairs);
        }
        regexStringBuilder.replace(0, regexStringBuilder.length(), regexCurrentState);
        capturedStringBuilder.replace(0, capturedStringBuilder.length(), csCurrentState);
    }

    private String getAsSingleRegex(Map<Integer, List<Pair<String, String>>> idCSRegexMap) {
        StringBuilder regexBuilder = new StringBuilder();
        for (Integer id : idCSRegexMap.keySet()) {
            List<Pair<String, String>> csRegexPairs = idCSRegexMap.get(id);
            if (csRegexPairs.size() > 1) {
                regexBuilder.append(OPENING_BRACES);
            }
            for (int i = 0; i < csRegexPairs.size(); i++) {
                Pair<String, String> csRegexPair = csRegexPairs.get(i);
                regexBuilder.append(csRegexPair.getSecond());
                if (i != csRegexPairs.size() - 1) {
                    regexBuilder.append(OR);
                }
            }
            if (csRegexPairs.size() > 1) {
                regexBuilder.append(CLOSING_BRACES);
            }
        }
        return regexBuilder.toString();
    }

    private synchronized TokenStream getTokenStream(String indexedField, String queryText) {
        TokenStream stream = queryAnalyzer.tokenStream(indexedField, new StringReader(queryText));
        return stream;
    }

    @Override
    public void close() throws IOException {
        if (queryAnalyzer != null) {
            queryAnalyzer.close();
        }
        LOGGER.info("Total number of Search Queries fired:" + totalNumOfQueriesSearched.get());
        LOGGER.info("Number of times Queries got split:" + numOfTimesQueriesSplit.get());
    }

    class Result {
        String regexQuery;
        String capturedString;
        TopDocs topDocs;

        Result(String regexQuery, String capturedString, TopDocs topDocs) {
            this.regexQuery = regexQuery;
            this.capturedString = capturedString;
            this.topDocs = topDocs;
        }
    }

}
