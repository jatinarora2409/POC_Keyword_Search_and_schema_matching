package com.infa.products.discovery.automatedclassification.engine.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;

import java.util.regex.Pattern;

/**
 * Analyzer for BusinessTermName field
 *
 * This analyzer performs the following operation on tokenStream
 * 1. lower casing
 * 2. underscores and whitespaces is replaced with SPACE.
 */
public class BusinessTermNameAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new KeywordTokenizer();
        TokenStream filter = source;
        filter = new LowerCaseFilter(source);
		filter=specialCharactersFilter(filter);  //Filter to remove special characters (optional)
        return new TokenStreamComponents(source, filter);
    }

    private TokenStream specialCharactersFilter(TokenStream filter) {

        Pattern underscore = Pattern.compile("[_]");
        Pattern whitespace = Pattern.compile("\\s+");
        boolean replaceAll = Boolean.TRUE;
        filter = new PatternReplaceFilter(filter, underscore, " ", replaceAll);  //replacing underscore with whitespace
        filter = new PatternReplaceFilter(filter, whitespace, " ", replaceAll); //replacing multiple whitespaces with a single whitespace
        return filter;
    }
}

