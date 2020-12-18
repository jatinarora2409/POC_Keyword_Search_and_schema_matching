package com.infa.products.discovery.automatedclassification.engine.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlignmentQueryAnalyzer extends Analyzer {
    private final SynonymMap synonymMap;

    private final Logger LOGGER = LoggerFactory.getLogger(AlignmentQueryAnalyzer.class);

    public AlignmentQueryAnalyzer(SynonymMap synonymMap) {
        this.synonymMap = synonymMap;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new WhitespaceTokenizer();
        TokenStream filter = new LowerCaseFilter(source);
        try {
            if (synonymMap != null && synonymMap.fst != null) {
                filter = new SynonymGraphFilter(filter, synonymMap, true);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to create SynonymGraphFilter " + e.getMessage());
        }
        return new TokenStreamComponents(source, filter);
    }
}
