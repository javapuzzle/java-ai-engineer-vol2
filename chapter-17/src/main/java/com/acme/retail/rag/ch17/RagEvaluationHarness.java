package com.acme.retail.rag.ch17;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Runs a golden dataset through all three scorers and averages each axis
 * independently -- this is the "RAGAS-style" idea this chapter is
 * illustrating: automated, repeatable scoring on a fixed dataset, with a
 * gate a build can fail, replacing "does this feel better" with a number.
 */
@Service
public class RagEvaluationHarness {

    private final ContextRecallScorer recallScorer;
    private final FaithfulnessScorer faithfulnessScorer;
    private final AnswerRelevanceScorer relevanceScorer;

    public RagEvaluationHarness(ContextRecallScorer recallScorer,
                                 FaithfulnessScorer faithfulnessScorer,
                                 AnswerRelevanceScorer relevanceScorer) {
        this.recallScorer = recallScorer;
        this.faithfulnessScorer = faithfulnessScorer;
        this.relevanceScorer = relevanceScorer;
    }

    public EvaluationSummary evaluate(List<GoldenCase> goldenCases) {
        double avgRecall = average(goldenCases, recallScorer::score);
        double avgFaithfulness = average(goldenCases, faithfulnessScorer::score);
        double avgRelevance = average(goldenCases, relevanceScorer::score);

        return new EvaluationSummary(avgRecall, avgFaithfulness, avgRelevance);
    }

    public boolean meetsQualityGate(EvaluationSummary summary,
                                     double minRecall,
                                     double minFaithfulness,
                                     double minRelevance) {
        return summary.averageContextRecall() >= minRecall
                && summary.averageFaithfulness() >= minFaithfulness
                && summary.averageAnswerRelevance() >= minRelevance;
    }

    private double average(List<GoldenCase> goldenCases, java.util.function.ToDoubleFunction<GoldenCase> scorer) {
        return goldenCases.stream().mapToDouble(scorer).average().orElse(0.0);
    }
}
