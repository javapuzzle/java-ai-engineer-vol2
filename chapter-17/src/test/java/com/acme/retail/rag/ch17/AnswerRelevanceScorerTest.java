package com.acme.retail.rag.ch17;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class AnswerRelevanceScorerTest {

    private final AnswerRelevanceScorer scorer = new AnswerRelevanceScorer();

    @Test
    void scoresHighRelevanceWhenTheAnswerAddressesTheQuestionAsked() {
        GoldenCase goldenCase = new GoldenCase(
                "What is the warranty period for the ProBlend500?",
                List.of(),
                List.of("irrelevant for this test"),
                "The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty.");

        assertThat(scorer.score(goldenCase)).isCloseTo(2.0 / 3, within(1e-9));
    }

    @Test
    void scoresZeroRelevanceWhenTheAnswerIsPerfectlyGroundedButOffTopic() {
        GoldenCase offTopicButFaithfulCase = new GoldenCase(
                "What is the warranty period for the ProBlend500?",
                List.of(),
                List.of("Standard shipping takes 3 to 5 business days within the continental US."),
                "Standard shipping takes 3 to 5 business days within the continental US.");

        assertThat(scorer.score(offTopicButFaithfulCase)).isCloseTo(0.0, within(1e-9));
    }
}
