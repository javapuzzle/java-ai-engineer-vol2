package com.acme.retail.rag.ch19;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Compares negative-feedback rate between two time windows -- the actual
 * signal this chapter's story hinges on: retrieval quality decaying
 * silently as the catalog changes, with the first real evidence being a
 * spike in "that wasn't helpful" feedback clicks, not a dashboard someone
 * happened to be watching.
 */
@Service
public class RetrievalQualityMonitor {

    public double negativeFeedbackRate(List<RetrievalOutcome> outcomes) {
        if (outcomes.isEmpty()) {
            return 0.0;
        }

        long negativeCount = outcomes.stream().filter(RetrievalOutcome::gotNegativeFeedback).count();
        return (double) negativeCount / outcomes.size();
    }

    public boolean isDegrading(List<RetrievalOutcome> baselineWindow,
                                List<RetrievalOutcome> recentWindow,
                                double maxAcceptableIncrease) {
        double baselineRate = negativeFeedbackRate(baselineWindow);
        double recentRate = negativeFeedbackRate(recentWindow);

        return (recentRate - baselineRate) > maxAcceptableIncrease;
    }
}
