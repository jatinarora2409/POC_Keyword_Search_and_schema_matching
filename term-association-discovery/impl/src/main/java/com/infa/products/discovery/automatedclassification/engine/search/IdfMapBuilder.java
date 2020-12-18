package com.infa.products.discovery.automatedclassification.engine.search;

import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IdfMapBuilder {

    private static final String TOKENIZED_BUSINESS_TERM = "tokenizedbusinesstermname";

    private String indexedField;
    private IndexReader reader;

    public IdfMapBuilder(IndexReader reader) throws IOException {
        this.indexedField = TOKENIZED_BUSINESS_TERM;
        this.reader = reader;
    }

    // This is an in-memory map, alternatively we can fire query to fetch idf while scoring.
    public Map<String, Float> getTfIdfMap() throws IOException {
         Map<String, Float> idfMap = new HashMap<>();
        /** GET FIELDS **/
        Fields fields = MultiFields.getFields(reader);

        TFIDFSimilarity tfidfSIM = new ClassicSimilarity();

        for (String field : fields) {
            if (field.equals(indexedField)) {
                TermsEnum termEnum = MultiFields.getTerms(reader, field).iterator();
                BytesRef bytesRef;
                while ((bytesRef = termEnum.next()) != null) {
                    if (termEnum.seekExact(bytesRef)) {
                        String term = bytesRef.utf8ToString();
                        float idf = tfidfSIM.idf(termEnum.docFreq(), reader.numDocs());
                        idfMap.put(term, idf);
                    }
                }
            }
        }
        return idfMap;
    }


}
