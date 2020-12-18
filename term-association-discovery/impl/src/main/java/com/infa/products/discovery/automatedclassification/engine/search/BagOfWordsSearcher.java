package com.infa.products.discovery.automatedclassification.engine.search;

import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.model.Asset;
import com.infa.products.discovery.automatedclassification.model.BusinessTerm;
import com.infa.products.discovery.automatedclassification.model.Field;
import com.infa.products.discovery.automatedclassification.model.SearchResult;
import com.infa.products.discovery.automatedclassification.model.api.DocumentBuilder;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.util.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class implementing Bag-Of-Words searching algorithm
 *
 * @author vshivam
 */
public class BagOfWordsSearcher implements Searcher<BusinessTerm> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BagOfWordsSearcher.class);

    private final IndexSearcher searcher;
    private final Analyzer queryAnalyzer;
    private final DocumentType<BusinessTerm> docType;

    // TODO: Add this to some constant class.
    // indexing field used for Bag-Of-Words match algorithm.
    private static final String TOKENIZED_BUSINESS_TERM = "tokenizedbusinesstermname";

    public BagOfWordsSearcher(IndexReader reader, Analyzer queryAnalyzer, DocumentType<BusinessTerm> docType) {
        this.searcher = new IndexSearcher(reader);
        this.queryAnalyzer = queryAnalyzer;
        this.docType = docType;
    }

    @Override
    public List<SearchResult<BusinessTerm>> search(Asset asset, int maxResults) throws DocumentSearchException {
        String indexedField = TOKENIZED_BUSINESS_TERM;
        String queryText = asset.getName();

        QueryBuilder queryBuilder = new QueryBuilder(queryAnalyzer);
        queryBuilder.setAutoGenerateMultiTermSynonymsPhraseQuery(true);

        /*
         *  Query to fetch documents where each term (or its synonym) of the asset, must be present.
         */
        Query bagOfWordsQuery = queryBuilder.createBooleanQuery(indexedField, queryText, BooleanClause.Occur.MUST);
        LOGGER.debug("bagOfWordsQuery: " + bagOfWordsQuery);
        TopDocs topDocs;
        List<SearchResult<BusinessTerm>> searchResults;
        try {
            topDocs = searcher.search(bagOfWordsQuery, maxResults);
            searchResults = processResults(topDocs, indexedField, bagOfWordsQuery);
        } catch (IOException e) {
            LOGGER.error("Error retrieving document." + e);
            throw new DocumentSearchException("Error retrieving document." + e);
        }

        return searchResults;
    }

    @Override
    public void close() {
        if (queryAnalyzer != null) {
            queryAnalyzer.close();
        }
    }

    List<SearchResult<BusinessTerm>> processResults(TopDocs topDocs, String indexedField, Query bagOfWordsQuery) {
        List<SearchResult<BusinessTerm>> searchResults = new ArrayList<>();
        ScoreDoc[] hits = topDocs.scoreDocs;
        try {
            for (int i = 0; i < hits.length; i++) {
                Document document = searcher.doc(hits[i].doc);
                final BusinessTerm businessTermDoc = getBusinessTermDoc(document);
                final String[] businessTermNames = document.getValues(indexedField);
                for (final String businessTermName : businessTermNames) {

                    List<Term> hitTerms = new ArrayList<>();
                    getHitTerms(bagOfWordsQuery, searcher, hits[i].doc, hitTerms);

                    StringBuilder sb = new StringBuilder();
                    for (Term term: hitTerms) {
                        sb.append(term.text()).append(" ");
                    }

                    String capturedString = sb.toString().trim();

                    final com.infa.products.discovery.automatedclassification.model.Query query =
                            new com.infa.products.discovery.automatedclassification.model.Query(indexedField, capturedString);
                    final SearchResult<BusinessTerm> searchResult = SearchResult.<BusinessTerm>builder().withQuery(query)
                            .withDocument(businessTermDoc).withMatchedString(businessTermName).build();
                    searchResults.add(searchResult);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error retrieving document." + e);
        }
        return searchResults;
    }

    private BusinessTerm getBusinessTermDoc(Document document) {
        DocumentBuilder<BusinessTerm> docBuilder = docType.getDocumentBuilder();
        for (Field f : docType.getFields()) {
            docBuilder.withField(f.getFieldName(), document.getValues(f.getFieldName()));
        }
        return docBuilder.build();
    }


    /**
     * This function gets the list of hit terms out of all terms present in the query.
     */
    void getHitTerms(Query query, IndexSearcher searcher, int docId, List<Term> hitTerms) throws IOException
    {
        if(query instanceof TermQuery)
        {
            if (searcher.explain(query, docId).isMatch())
                hitTerms.add(((TermQuery) query).getTerm());
            return;
        }
        if(query instanceof BooleanQuery)
        {
            for (BooleanClause clause : (BooleanQuery)query) {
                getHitTerms(clause.getQuery(), searcher, docId,hitTerms);
            }
            return;
        }
        if (query instanceof MultiTermQuery)
        {
            if (!(query instanceof FuzzyQuery)) //FuzzQuery doesn't support SetRewriteMethod
                ((MultiTermQuery)query).setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_REWRITE);
            getHitTerms(query.rewrite(searcher.getIndexReader()), searcher, docId,hitTerms);
        }
        if(query instanceof PhraseQuery) {
            if (searcher.explain(query, docId).isMatch())
                for (Term term: ((PhraseQuery) query).getTerms()) {
                    hitTerms.add(term);
                }
            return;
        }
        if(query instanceof SynonymQuery) {
            for (Term term: ((SynonymQuery) query).getTerms()) {
                getHitTerms(new TermQuery(term), searcher, docId, hitTerms);
            }
        }
    }

}
