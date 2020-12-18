package com.infa.products.discovery.automatedclassification.engine.search;

import com.infa.products.discovery.automatedclassification.engine.search.AlignmentRegex.RegexGroupType;
import com.infa.products.discovery.automatedclassification.engine.search.QueryAutomatonBuilder.TokenInfo;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.Iterator;

public class AlignmentQueryBuilder {
    private static final String SYNONYM="SYNONYM";
    private static final String CONTENTS="contents";

	public static AlignmentRegex getRegex(String field, TokenStream source) throws IOException {
		QueryAutomatonBuilder graph = new QueryAutomatonBuilder(source);
		AlignmentRegex regexBuilder = new AlignmentRegex();
		int[] articulationPoints = graph.articulationPoints();
		int lastState = 0;
		for (int i = 0; i <= articulationPoints.length; i++) {
			int start = lastState;
			int end = -1;
			if (i < articulationPoints.length) {
				end = articulationPoints[i];
			}
			lastState = end;
			final Query queryPos = null;
			if (i > 0) {
				regexBuilder.advance();
			}
			if (graph.hasSidePath(start)) {
				final Iterator<TokenStream> it = graph.getFiniteStrings(start, end);
				boolean first = true;
				while (it.hasNext()) {
					regexBuilder.beginTokenSequence();
					TokenStream ts = it.next();
					CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
					PositionIncrementAttribute posIncr = ts.addAttribute(PositionIncrementAttribute.class);
					PositionLengthAttribute posLengthAtt = ts.addAttribute(PositionLengthAttribute.class);
					TermToBytesRefAttribute termAtt = ts.getAttribute(TermToBytesRefAttribute.class);
					OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
					TypeAttribute type = ts.addAttribute(TypeAttribute.class);

					int position = start;
					ts.reset();
					while (ts.incrementToken()) {
						int increment = posIncr.getPositionIncrement();
						int posLength = posLengthAtt.getPositionLength();
						if (increment > 0) {
							position = position + increment;
							//System.out.println();
							//System.out.print(position + ": ");
						}
						//System.out.print("[" + term.toString() + ":" + offset.startOffset() + "->" + offset.endOffset()
						//		+ ":" + type.type() + ":" + posLength + "]");
						if (type.type().equals(SYNONYM)) {
							regexBuilder.addToken(term.toString(), RegexGroupType.WORD);
						} else {
							String text = term.toString();
							regexBuilder.addToken(text, RegexGroupType.ABBREVIATION);
						}
					}
					regexBuilder.endTokenSequence();
				}
				// queryPos = newGraphSynonymQuery(queries);
			} else {
				TokenInfo[] terms = graph.getTerms(CONTENTS, start);
				assert terms.length > 0;
				if (terms.length == 1) {
					regexBuilder.beginTokenSequence();
					regexBuilder.addToken(terms[0].getText(), RegexGroupType.ABBREVIATION);
					regexBuilder.endTokenSequence();
					// queryPos = null;// newTermQuery(terms[0]);
				} else {
					for (int j = 0; j < terms.length; j++) {
						TokenInfo term = terms[j];
						regexBuilder.beginTokenSequence();
						regexBuilder.addToken(term.getText(),
								term.getType().equalsIgnoreCase(SYNONYM) ? RegexGroupType.WORD
										: RegexGroupType.ABBREVIATION);
						regexBuilder.endTokenSequence();
					}
					// queryPos = null;// newSynonymQuery(terms);
				}
			}
			if (queryPos != null) {
				// builder.add(queryPos, operator);
			}

		}
		regexBuilder.done();
		return regexBuilder;
	}
}
