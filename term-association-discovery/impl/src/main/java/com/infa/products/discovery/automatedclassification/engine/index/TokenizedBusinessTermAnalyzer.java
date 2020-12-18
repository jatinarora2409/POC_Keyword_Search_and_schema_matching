package com.infa.products.discovery.automatedclassification.engine.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.apache.lucene.analysis.pattern.PatternTokenizer;

import java.util.regex.Pattern;


/**
 * Analyzer for TokenizedBusinessTerm field
 *
 * Following operations are performed by this analyzer:
 * 1. tokenize on whitespaces and special characters except for "%" and "'"
 * 2. replace "'" with empty string
 * 3. lowerCasing
 */
public class TokenizedBusinessTermAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new PatternTokenizer(Pattern.compile("[^A-Za-z0-9%']"), -1);//tokenizes on whitespace and special characters except ' and %
        TokenStream filter = new PatternReplaceFilter(source, Pattern.compile("[']"), "", Boolean.TRUE);//replaces apostrophe with empty string
        filter = new LowerCaseFilter(filter);

        return new TokenStreamComponents(source, filter);
    }
}