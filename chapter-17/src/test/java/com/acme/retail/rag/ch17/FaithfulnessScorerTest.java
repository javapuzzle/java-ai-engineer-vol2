package com.acme.retail.rag.ch17;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class FaithfulnessScorerTest {

    private final FaithfulnessScorer scorer = new FaithfulnessScorer();

    @Test
    void scoresPerfectFaithfulnessWhenEverySentenceIsGrounded() {
        GoldenCase goldenCase = new GoldenCase(
                "What is the warranty period for the ProBlend500?",
                List.of("ProBlend500", "discontinued", "no longer covered"),
                List.of("The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty support."),
                "The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty.");

        assertThat(scorer.score(goldenCase)).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void scoresHalfFaithfulnessWhenOneOfTwoSentencesIsInvented() {
        GoldenCase goldenCase = new GoldenCase(
                "What is the warranty on the XR-4400?",
                List.of("XR-4400", "2-year", "warranty"),
                List.of("The XR-4400 blender has a 2-year manufacturer's warranty covering defects in materials and workmanship."),
                "The XR-4400 blender has a 2-year manufacturer's warranty covering defects in materials and workmanship. "
                        + "It also includes free overnight shipping on all replacement parts.");

        assertThat(scorer.score(goldenCase)).isCloseTo(0.5, within(1e-9));
    }
}
