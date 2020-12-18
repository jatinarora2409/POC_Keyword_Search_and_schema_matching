package com.infa.products.discovery.automatedclassification;

import com.infa.products.discovery.automatedclassification.engine.search.TfIdfTermScorer;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.InvalidAlignmentCandidatesException;
import com.infa.products.discovery.automatedclassification.similarity.AbbreviationAlignmentScorer;
import com.infa.products.discovery.automatedclassification.similarity.AlignmentComputerFactory;
import com.infa.products.discovery.automatedclassification.similarity.api.Alignment;
import com.infa.products.discovery.automatedclassification.similarity.api.AlignmentComputer;
import com.infa.products.discovery.automatedclassification.util.ConfigUtil;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

public class AlignmentScoreTest {

    Set<String> ENGLISH_STOP_WORDS = new HashSet<>();

    @BeforeClass
    public void setUp() throws DocumentSearchException {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);

        ENGLISH_STOP_WORDS.addAll(ENGLISH_STOP_WORD_SET);
    }

    @Test
    public void alignmentScoreTest() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("social", 0.34f);
        idfMap.put("security", 0.5f);
        idfMap.put("number", 0.67f);

        AlignmentComputer alignmentComputer = AlignmentComputerFactory
                .getAlignmentComputer(new AbbreviationAlignmentScorer(ENGLISH_STOP_WORDS, new TfIdfTermScorer(idfMap)));
        Alignment alignment = alignmentComputer.compute("social security number", "ssn");
        System.out.println(alignment.toString());
        assert alignment.equals("s------s--------n-----");

        float actualScore = alignment.getScore();
        float expectedWordMatchRatio = 1;
        float expectedCharMatchRatio = (3-3)/3;
        float expectedScore = 90*expectedWordMatchRatio + 10*expectedCharMatchRatio;

        Assert.assertEquals(actualScore, expectedScore);
    }

    @Test
    public void alignmentScoreTest1() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("sss", 0.34f);

        AlignmentComputer alignmentComputer = AlignmentComputerFactory
                .getAlignmentComputer(new AbbreviationAlignmentScorer(ENGLISH_STOP_WORDS, new TfIdfTermScorer(idfMap)));
        Alignment alignment = alignmentComputer.compute("sss sss sss", "sss");
        System.out.println(alignment.toString());
        assert alignment.equals("s---s---s--");

        float actualScore = alignment.getScore();
        float expectedWordMatchRatio = 1f;
        float expectedCharMatchRatio = 0f;
        float expectedScore = 90*expectedWordMatchRatio + 10*expectedCharMatchRatio;

        Assert.assertEquals(actualScore, expectedScore);
    }

    @Test
    public void alignmentScoreTest2() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("busy", 0.2f);
        idfMap.put("business", 0.5f);
        idfMap.put("back", 0.7f);

        TfIdfTermScorer tfIdfTermScorer = new TfIdfTermScorer(idfMap);

        AlignmentComputer alignmentComputer = AlignmentComputerFactory
                .getAlignmentComputer(new AbbreviationAlignmentScorer(ENGLISH_STOP_WORDS, tfIdfTermScorer));
        String longForm = "busy business back";
        Alignment alignment = alignmentComputer.compute(longForm, "bu bu");
        System.out.println(alignment.toString());
        assert alignment.equals("bu-- bu-----------");

        float actualScore = alignment.getScore();

        Map<String, Float> tfIdfScoreMap = tfIdfTermScorer.getScoreMap(Arrays.asList(longForm.split("\\s+")));

        float expectedWordMatchRatio = (tfIdfScoreMap.get("busy") + tfIdfScoreMap.get("business")) / (tfIdfScoreMap.get("busy") + tfIdfScoreMap.get("business") + tfIdfScoreMap.get("back"));
        float expectedCharMatchRatio = (5f-2f)/(longForm.length()-3f);
        float expectedScore = 90*expectedWordMatchRatio + 10*expectedCharMatchRatio;

        System.out.println(longForm.length());
        System.out.println("expectedWordMatchRatio: " + expectedWordMatchRatio);
        System.out.println("expectedCharMatchRatio:" + expectedCharMatchRatio);

        Assert.assertEquals(actualScore, expectedScore);
    }

    @Test
    public void alignmentScoreTest3() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("metric", 0.2f);
        idfMap.put("name", 0.5f);
        idfMap.put("identifier", 0.7f);
        idfMap.put("code", 0.4f);

        TfIdfTermScorer tfIdfTermScorer = new TfIdfTermScorer(idfMap);

        AlignmentComputer alignmentComputer = AlignmentComputerFactory
                .getAlignmentComputer(new AbbreviationAlignmentScorer(ENGLISH_STOP_WORDS, tfIdfTermScorer));
        String longForm = "metric name identifier code";
        Alignment alignment = alignmentComputer.compute(longForm, "mtrc nm idnt");

        assert alignment.equals("m-tr-c n-m- id-nt----------");

        float actualScore = alignment.getScore();

        Map<String, Float> tfIdfScoreMap = tfIdfTermScorer.getScoreMap(Arrays.asList(longForm.split("\\s+")));

        float expectedWordMatchRatio = (tfIdfScoreMap.get("metric") + tfIdfScoreMap.get("name") + tfIdfScoreMap.get("identifier")) /
                (tfIdfScoreMap.get("metric") + tfIdfScoreMap.get("name") + tfIdfScoreMap.get("identifier") + tfIdfScoreMap.get("code"));
        float expectedCharMatchRatio = (12f-3f)/(longForm.length()-4f);
        float expectedScore = 90*expectedWordMatchRatio + 10*expectedCharMatchRatio;

        System.out.println(longForm.length());
        System.out.println("expectedWordMatchRatio: " + expectedWordMatchRatio);
        System.out.println("expectedCharMatchRatio:" + expectedCharMatchRatio);

        Assert.assertEquals(actualScore, expectedScore);
    }

}
