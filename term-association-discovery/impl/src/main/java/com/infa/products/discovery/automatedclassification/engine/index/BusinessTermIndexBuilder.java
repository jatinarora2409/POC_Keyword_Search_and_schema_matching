package com.infa.products.discovery.automatedclassification.engine.index;

import com.infa.products.discovery.automatedclassification.engine.api.IndexBuilder;
import com.infa.products.discovery.automatedclassification.exception.IndexWriterException;
import com.infa.products.discovery.automatedclassification.model.BusinessTerm;
import com.infa.products.discovery.automatedclassification.model.BusinessTermType;
import com.infa.products.discovery.automatedclassification.model.Field;
import com.infa.products.discovery.automatedclassification.model.api.DocumentReader;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Index builder for BusinessTermType document.
 *
 * @author vshivam
 */
public class BusinessTermIndexBuilder implements IndexBuilder<BusinessTerm> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessTermIndexBuilder.class);

    private final DocumentType<BusinessTerm> docType;

    private final Directory businessTermDirectory;

    private final Analyzer businessTermAnalyzer;
    private final Analyzer tokenizedBusinessTermAnalyzer;

    private IndexWriter businessTermWriter;

    // indexing field used for Bag-Of-Words match algorithm.
    // TODO: put this constant to some place from it can be shared with Indexer and Searcher
    static final String TOKENIZED_BUSINESS_TERM = "tokenizedbusinesstermname";

    public BusinessTermIndexBuilder(DocumentType<BusinessTerm> docType, Directory directory) throws IndexWriterException {
        this.docType = docType;
        this.businessTermDirectory = directory;
        this.businessTermAnalyzer = new BusinessTermNameAnalyzer();
        this.tokenizedBusinessTermAnalyzer = new TokenizedBusinessTermAnalyzer();
        initializeIndexWriter();
    }

    /**
     * Initialize the indexWriter with per field analyzers.
     * @throws IndexWriterException
     */
    private void initializeIndexWriter() throws IndexWriterException {
        Map<String, Analyzer> fieldAnalyzers = new HashMap<>();
        fieldAnalyzers.put(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), businessTermAnalyzer);
        fieldAnalyzers.put(TOKENIZED_BUSINESS_TERM, tokenizedBusinessTermAnalyzer);
        PerFieldAnalyzerWrapper analyzerWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), fieldAnalyzers);

        IndexWriterConfig businessTermConfig = new IndexWriterConfig(analyzerWrapper);
        businessTermConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try {
            businessTermWriter = new IndexWriter(businessTermDirectory, businessTermConfig);
        } catch (IOException e) {
            throw new IndexWriterException("Index initialization failed.", e);
        }
    }

    @Override
    public void indexDocuments(Iterator<BusinessTerm> docs) throws IndexWriterException {
        DocumentReader<BusinessTerm> docReader = docType.getDocumentReader();
        try {
            while (docs.hasNext()) {
                BusinessTerm doc = docs.next();
                final Document businessTermDoc = new Document();

                for (Field f : docType.getFields()) {
                    if (f.getType().equals(Field.Type.TITLE)) {
                        if (f.getFieldName().equals(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName())) {
                            final String fieldValue = (String) docReader.getFieldValue(doc, f.getFieldName());
                            businessTermDoc.add(new TextField(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), fieldValue, Store.YES));
                            businessTermDoc.add(new TextField(TOKENIZED_BUSINESS_TERM, fieldValue, Store.YES));
                        }
                        else if (f.getFieldName().equals(BusinessTermType.BUSINESS_TERM_SYNONYMS_FIELD.getFieldName())) {
                            final Set<String> aliases = (Set<String>) docReader.getFieldValue(doc, f.getFieldName());
                            for (final String fieldValue : aliases) {
                                businessTermDoc.add(new TextField(BusinessTermType.BUSINESS_TERM_NAME_FIELD.getFieldName(), fieldValue, Store.YES));
                                businessTermDoc.add(new TextField(TOKENIZED_BUSINESS_TERM, fieldValue, Store.YES));
                            }
                        }
                    } else {
                        businessTermDoc.add(new StringField(f.getFieldName(), docReader.getFieldValue(doc, f.getFieldName()).toString(), Store.YES));
                    }
                }

                businessTermWriter.addDocument(businessTermDoc);
            }
            logRAMUsageForIndex();
        } catch (IOException e) {
            throw new IndexWriterException("Indexing operation failed.", e);
        }
        finally {
            try {
                businessTermWriter.commit();
            } catch (IOException e) {
                throw  new IndexWriterException(e.getMessage());
            }

        }
    }

    @Override
    public void close() throws IOException {
        if (businessTermWriter != null) {
            businessTermWriter.close();
        }
    }

    /*
    Log ram usage if RAM directory is used.
     */
    private void logRAMUsageForIndex() {
        if (businessTermDirectory instanceof RAMDirectory) {
            RAMDirectory ramDirectory = (RAMDirectory) businessTermDirectory;
            LOGGER.info("RAM used to store index:" + ramDirectory.ramBytesUsed() + " bytes.");
        }
    }
}
