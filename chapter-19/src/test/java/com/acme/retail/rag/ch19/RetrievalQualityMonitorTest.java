package com.acme.retail.rag.ch19;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class RetrievalQualityMonitorTest {

    private final RetrievalQualityMonitor monitor = new RetrievalQualityMonitor();

    private List<RetrievalOutcome> windowWithNegativeRate(int totalOutcomes, int negativeCount) {
        List<RetrievalOutcome> outcomes = new ArrayList<>();
        IntStream.range(0, negativeCount)
                .forEach(i -> outcomes.add(new RetrievalOutcome("query-" + i, true)));
        IntStream.range(negativeCount, totalOutcomes)
                .forEach(i -> outcomes.add(new RetrievalOutcome("query-" + i, false)));
        return outcomes;
    }

    @Test
    void computesNegativeFeedbackRateAsASimpleFraction() {
        List<RetrievalOutcome> outcomes = windowWithNegativeRate(10, 1);

        assertThat(monitor.negativeFeedbackRate(outcomes)).isCloseTo(0.1, within(1e-9));
    }

    @Test
    void aLargeJumpInNegativeFeedbackRateIsFlaggedAsDegrading() {
        List<RetrievalOutcome> baselineWindow = windowWithNegativeRate(10, 1);
        List<RetrievalOutcome> recentWindow = windowWithNegativeRate(10, 6);

        boolean degrading = monitor.isDegrading(baselineWindow, recentWindow, 0.15);

        assertThat(degrading)
                .as("baseline rate 0.1 vs recent rate 0.6 is a 0.5 increase, well past a 0.15 tolerance")
                .isTrue();
    }

    @Test
    void aSmallFluctuationInNegativeFeedbackRateIsNotFlaggedAsDegrading() {
        List<RetrievalOutcome> baselineWindow = windowWithNegativeRate(10, 1);
        List<RetrievalOutcome> recentWindow = windowWithNegativeRate(10, 2);

        boolean degrading = monitor.isDegrading(baselineWindow, recentWindow, 0.15);

        assertThat(degrading)
                .as("baseline rate 0.1 vs recent rate 0.2 is only a 0.1 increase, within a 0.15 tolerance")
                .isFalse();
    }
}
