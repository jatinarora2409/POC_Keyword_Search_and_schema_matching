package com.infa.products.discovery.automatedclassification;

import com.infa.products.discovery.automatedclassification.engine.search.TfIdfTermScorer;
import com.infa.products.discovery.automatedclassification.exception.DocumentSearchException;
import com.infa.products.discovery.automatedclassification.exception.InvalidAlignmentCandidatesException;
import com.infa.products.discovery.automatedclassification.similarity.BagOfWordsAlignmentComputer;
import com.infa.products.discovery.automatedclassification.similarity.api.Alignment;
import com.infa.products.discovery.automatedclassification.similarity.api.AlignmentComputer;
import com.infa.products.discovery.automatedclassification.util.ConfigUtil;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.infa.products.discovery.automatedclassification.util.Constants.ENGLISH_STOP_WORD_SET;

public class BagOfWordsScoreTest {

    Set<String> ENGLISH_STOP_WORDS = new HashSet<>();
    @BeforeClass
    public void setUp() throws DocumentSearchException {
        ConfigUtil.setEnglishStopWordSet(ENGLISH_STOP_WORD_SET);

        ENGLISH_STOP_WORDS.addAll(ENGLISH_STOP_WORD_SET);
    }

    @Test
    public void BOWScoreTest() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("social", 0.34f);
        idfMap.put("security", 0.5f);
        idfMap.put("number", 0.67f);
        idfMap.put("of", 0.01f);

        AlignmentComputer alignmentComputer = new BagOfWordsAlignmentComputer(ENGLISH_STOP_WORDS, new TfIdfTermScorer(idfMap));
        Alignment alignment = alignmentComputer.compute("number of social security", "social security number");
        System.out.println(alignment.toString());

        float actualScore = alignment.getScore();
        float expectedWordMatchRatio = 1f;
        float expectedCharMatchRatio = (6f + 8f + 6f) / (6f + 6f + 8f);
        float expectedScore = 90 * expectedWordMatchRatio + 10 * expectedCharMatchRatio;
        System.out.println(actualScore);
        Assert.assertEquals(actualScore, expectedScore);
    }

    @Test
    public void BOWScoreTest1() throws InvalidAlignmentCandidatesException {

        Map<String, Float> idfMap = new HashMap<>();
        idfMap.put("sss", 0.34f);

        AlignmentComputer alignmentComputer = new BagOfWordsAlignmentComputer(ENGLISH_STOP_WORDS, new TfIdfTermScorer(idfMap));
        Alignment alignment = alignmentComputer.compute("sss sss sss", "sss");
        System.out.println(alignment.toString());

        float actualScore = alignment.getScore();
        float expectedWordMatchRatio = 1f / 3f;
        float expectedCharMatchRatio = (3f - 1f) / (9f - 3f);
        float expectedScore = 90 * expectedWordMatchRatio + 10 * expectedCharMatchRatio;
        System.out.println(actualScore);
        Assert.assertEquals(actualScore, expectedScore);
    }

}
