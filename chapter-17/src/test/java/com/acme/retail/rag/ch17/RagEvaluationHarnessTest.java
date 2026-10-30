package com.acme.retail.rag.ch17;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RagEvaluationHarnessTest {

    private final RagEvaluationHarness harness = new RagEvaluationHarness(
            new ContextRecallScorer(), new FaithfulnessScorer(), new AnswerRelevanceScorer());

    private static final GoldenCase GOOD_CASE = new GoldenCase(
            "What is the warranty period for the ProBlend500?",
            List.of("ProBlend500", "discontinued", "no longer covered"),
            List.of("The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty support."),
            "The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty.");

    private static final GoldenCase HALLUCINATING_CASE = new GoldenCase(
            "What is the warranty on the XR-4400?",
            List.of("XR-4400", "2-year", "warranty"),
            List.of("The XR-4400 blender has a 2-year manufacturer's warranty covering defects in materials and workmanship."),
            "The XR-4400 blender has a 2-year manufacturer's warranty covering defects in materials and workmanship. "
                    + "It also includes free overnight shipping on all replacement parts.");

    @Test
    void aBatchOfOnlyGoodCasesPassesTheQualityGate() {
        EvaluationSummary summary = harness.evaluate(List.of(GOOD_CASE));

        boolean passes = harness.meetsQualityGate(summary, 0.9, 0.9, 0.5);

        assertThat(passes).isTrue();
    }

    @Test
    void aSingleHallucinatingCaseDragsTheBatchAverageBelowTheFaithfulnessGate() {
        EvaluationSummary summary = harness.evaluate(List.of(GOOD_CASE, HALLUCINATING_CASE));

        assertThat(summary.averageFaithfulness())
                .as("one fully-faithful case (1.0) and one half-faithful case (0.5) average to 0.75")
                .isCloseTo(0.75, org.assertj.core.api.Assertions.within(1e-9));

        boolean passes = harness.meetsQualityGate(summary, 0.9, 0.9, 0.5);

        assertThat(passes)
                .as("0.75 average faithfulness fails a 0.9 minimum -- this is the regression the gate exists to catch")
                .isFalse();
    }

    @Test
    void recallAndRelevanceAloneDoNotCompensateForAFaithfulnessFailure() {
        EvaluationSummary summary = harness.evaluate(List.of(GOOD_CASE, HALLUCINATING_CASE));

        assertThat(summary.averageContextRecall()).isCloseTo(1.0, org.assertj.core.api.Assertions.within(1e-9));
        boolean passes = harness.meetsQualityGate(summary, 0.9, 0.9, 0.5);

        assertThat(passes)
                .as("recall is perfect, but the gate checks all three axes independently -- a strong recall can't hide a weak faithfulness score")
                .isFalse();
    }
}
