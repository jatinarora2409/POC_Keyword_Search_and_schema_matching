/**
 *
 */
package com.infa.products.discovery.automatedclassification;

import com.infa.products.discovery.automatedclassification.engine.search.TfIdfTermScorer;
import com.infa.products.discovery.automatedclassification.exception.InvalidAlignmentCandidatesException;
import com.infa.products.discovery.automatedclassification.similarity.AbbreviationAlignmentScorer;
import com.infa.products.discovery.automatedclassification.similarity.AlignmentComputerFactory;
import com.infa.products.discovery.automatedclassification.similarity.api.Alignment;
import com.infa.products.discovery.automatedclassification.similarity.api.AlignmentComputer;
import com.infa.products.discovery.automatedclassification.util.ConfigUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

/**
 *
 * @author ckakwani
 *
 */

public class AlignmentTest {

    Set<String> ENGLISH_STOP_WORDS = new HashSet<>();

    @BeforeClass
    public void setUp() {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);

        ENGLISH_STOP_WORDS.addAll(ENGLISH_STOP_WORD_SET);
    }

    @Test(testName = "testSimpleAlignment", enabled = true, groups = {
            "SimilarityTests"}, description = "Test simple alignment.")
    public void alignmentTest() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("social", 0.34f);
        idfMap.put("security", 0.5f);
        idfMap.put("number", 0.67f);

        AlignmentComputer alignmentComputer = AlignmentComputerFactory
                .getAlignmentComputer(new AbbreviationAlignmentScorer(ENGLISH_STOP_WORDS, new TfIdfTermScorer(idfMap)));
        Alignment alignment = alignmentComputer.compute("social security number", "ssn");
        System.out.println(alignment.toString());
        assert alignment.equals("s------s--------n-----");
    }

    @Test(testName = "testWordAlignment1", enabled = true, groups = {
            "SimilarityTests"}, description = "Test the alignment algorithm for a bias for matching first letters of words.")
    public void alignmentWordMatchTest1() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("sss", 0.34f);

        AlignmentComputer alignmentComputer = AlignmentComputerFactory
                .getAlignmentComputer(new AbbreviationAlignmentScorer(ENGLISH_STOP_WORDS, new TfIdfTermScorer(idfMap)));
        Alignment alignment = alignmentComputer.compute("sss sss sss", "sss");
        System.out.println(alignment.toString());
        assert alignment.equals("s---s---s--");
    }

    @Test(testName = "testWordAlignment2", enabled = true, groups = {
            "SimilarityTests"}, description = "Test the alignment algorithm for a bias for matching first letters of words.")
    public void alignmentWordMatchTest2() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("enterprise", 0.34f);
        idfMap.put("provider", 0.5f);
        idfMap.put("individual", 0.67f);
        idfMap.put("provider", 0.88f);
        idfMap.put("title", 0.22f);

        AlignmentComputer alignmentComputer = AlignmentComputerFactory
                .getAlignmentComputer(new AbbreviationAlignmentScorer(ENGLISH_STOP_WORDS, new TfIdfTermScorer(idfMap)));
        Alignment alignment = alignmentComputer.compute("enterprise provider individual provider title", "ep ip ttl");
        System.out.println(alignment.toString());
        assert alignment.equals("e----------p------- i----------p------- t-tl-");
    }

    @Test(testName = "alignmentStopWordMatchTest1", enabled = true, groups = {
            "SimilarityTests"}, description = "Test the alignment algorithm for matching phrases when stop words don't match.")
    public void alignmentStopWordMatchTest1() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("date", 0.34f);
        idfMap.put("of", 0.5f);
        idfMap.put("original", 0.67f);
        idfMap.put("protection", 0.88f);
        idfMap.put("value", 0.22f);

        AlignmentComputer alignmentComputer = AlignmentComputerFactory
                .getAlignmentComputer(new AbbreviationAlignmentScorer(ENGLISH_STOP_WORDS, new TfIdfTermScorer(idfMap)));
        Alignment alignment = alignmentComputer.compute("date of original protection value", "dt orgnl prtctn vl");
        System.out.println(alignment.toString());
        assert alignment.equals("d-t- ---or-g-n-l pr-t-ct--n v-l--");
    }

    @Test(testName = "alignmentStopWordMatchTest2", enabled = true, groups = {
            "SimilarityTests"}, description = "Test the alignment algorithm for matching phrases when stop words don't match.")
    public void alignmentStopWordMatchTest2() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("system", 0.34f);
        idfMap.put("of", 0.5f);
        idfMap.put("record", 0.67f);
        idfMap.put("protection", 0.88f);
        idfMap.put("value", 0.22f);

        AlignmentComputer alignmentComputer = AlignmentComputerFactory
                .getAlignmentComputer(new AbbreviationAlignmentScorer(ENGLISH_STOP_WORDS, new TfIdfTermScorer(idfMap)));
        Alignment alignment = alignmentComputer.compute("system of record", "sor");
        System.out.println(alignment.toString());
        assert alignment.equals("s------o--r-----");
    }

    @Test(testName = "invalidStopWordAlignmentTest", enabled = true, groups = {
            "SimilarityTests"}, description = "Test the alignment algorithm for invalid alignment.")
    public void invalidStopWordAlignmentTest() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("system", 0.34f);
        idfMap.put("of", 0.5f);
        idfMap.put("record", 0.67f);
        idfMap.put("protection", 0.88f);
        idfMap.put("value", 0.22f);

        AlignmentComputer alignmentComputer = AlignmentComputerFactory
                .getAlignmentComputer(new AbbreviationAlignmentScorer(ENGLISH_STOP_WORDS, new TfIdfTermScorer(idfMap)));
        Alignment alignment = alignmentComputer.compute("system for record", "sor");
        assert alignment == null;
    }

}
