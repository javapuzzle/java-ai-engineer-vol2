package com.acme.retail.rag.ch17;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ContextRecallScorerTest {

    private final ContextRecallScorer scorer = new ContextRecallScorer();

    @Test
    void scoresPerfectRecallWhenEveryRequiredFactAppearsInRetrievedContext() {
        GoldenCase goldenCase = new GoldenCase(
                "What is the warranty period for the ProBlend500?",
                List.of("ProBlend500", "discontinued", "no longer covered"),
                List.of("The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty support."),
                "The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty.");

        assertThat(scorer.score(goldenCase)).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void scoresPartialRecallWhenSomeRequiredFactsAreMissingFromRetrievedContext() {
        GoldenCase goldenCase = new GoldenCase(
                "What is the return policy for opened electronics?",
                List.of("30 days", "full refund", "opened"),
                List.of("Items may be returned within 30 days of purchase."),
                "Items may be returned within 30 days of purchase.");

        assertThat(scorer.score(goldenCase)).isCloseTo(1.0 / 3, within(1e-9));
    }

    @Test
    void treatsNoRequiredFactsAsTriviallyPerfectRecall() {
        GoldenCase goldenCase = new GoldenCase(
                "Anything", List.of(), List.of("Irrelevant context."), "Irrelevant answer.");

        assertThat(scorer.score(goldenCase)).isCloseTo(1.0, within(1e-9));
    }
}
