package com.infa.products.discovery.automatedclassification.engine;

import com.infa.products.discovery.automatedclassification.engine.api.IndexBuilder;
import com.infa.products.discovery.automatedclassification.engine.api.SearchEngine;
import com.infa.products.discovery.automatedclassification.engine.api.Searcher;
import com.infa.products.discovery.automatedclassification.engine.index.BusinessTermIndexBuilder;
import com.infa.products.discovery.automatedclassification.engine.search.BusinessTermSearcher;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.IndexWriterException;
import com.infa.products.discovery.automatedclassification.model.BusinessTerm;
import com.infa.products.discovery.automatedclassification.model.Synonym;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.CharsRef;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Search Engine for BusinessTerm.
 */
public class BusinessTermSearchEngine implements SearchEngine<BusinessTerm> {

    private final DocumentType<BusinessTerm> docType;

    private List<String> stopWordList;

    private List<String> ignoredPrefixList;

    private SynonymMap synonymMap;

    private final Directory directory;

    private IndexReader reader;


    public BusinessTermSearchEngine(DocumentType<BusinessTerm> type) {
        this.docType = type;
        Objects.requireNonNull(docType, "Document type cannot be null.");
        Objects.requireNonNull(docType.getFields(), "Document fields cannot be null.");
        if (docType.getFields().isEmpty()) {
            throw new IllegalArgumentException("Document fields cannot be empty.");
        }
        Objects.requireNonNull(docType.getDocumentReader(), "Document reader cannot be null.");
        Objects.requireNonNull(docType.getDocumentBuilder(), "Document builder cannot be null.");
        // Storing Indexes in RAM.
        directory = new RAMDirectory();

    }

    public void buildSynonymMap(Iterable<Synonym> synonyms) throws DocumentSearchException {
        SynonymMap.Builder builder = new SynonymMap.Builder(true);

        try {
            for (Synonym synonymEntry : synonyms) {
                String tokenGroup = synonymEntry.getPhrase();
                String synonymTokenGroup = synonymEntry.getSynonymPhrase();
                tokenGroup = BusinessTermSearcher.processSpecialChars(tokenGroup);
                synonymTokenGroup = BusinessTermSearcher.processSpecialChars(synonymTokenGroup);
                tokenGroup = tokenGroup.replaceAll("\\s+", Character.toString(SynonymMap.WORD_SEPARATOR));
                synonymTokenGroup = synonymTokenGroup.replaceAll("\\s+", Character.toString(SynonymMap.WORD_SEPARATOR));
                builder.add(new CharsRef(tokenGroup), new CharsRef(synonymTokenGroup), true);
            }

            this.synonymMap = builder.build();
        } catch (IOException e) {
            throw new DocumentSearchException(e.getMessage());
        }
    }


    public void setStopWordList(List<String> stopWordList) {
        this.stopWordList = stopWordList;
    }

    public void setPrefixesToIgnore(List<String> ignoredPrefixList) {
        this.ignoredPrefixList = ignoredPrefixList;
    }

    @Override
    public IndexBuilder<BusinessTerm> getIndexBuilder() throws IndexWriterException {
        return new BusinessTermIndexBuilder(docType, directory);
    }

    @Override
    public Searcher<BusinessTerm> getSearcher() throws IOException {
        try {
            reader = DirectoryReader.open(directory);
        } catch (IOException e) {
            throw e;
        }
        return new BusinessTermSearcher(reader, stopWordList, ignoredPrefixList, synonymMap);
    }

    @Override
    public void close() throws IOException {
        if (directory != null) {
            directory.close();
        }
        if (reader != null) {
            reader.close();
        }
    }
}
