package com.acme.retail.rag.ch14;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SingleChunkAnswerabilityCheckTest {

    private final SingleChunkAnswerabilityCheck check = new SingleChunkAnswerabilityCheck();

    private final Document coverageChunk = new Document(
            "plan-coverage", "PlanA covers ProBlend500, XR4400, and HP2210.", Map.of());
    private final Document eligibilityChunk = new Document(
            "loyalty-eligibility",
            "ProBlend500 and HP2210 are eligible for the TradeInX loyalty program. XR4400 is eligible for TradeInY instead.",
            Map.of());

    @Test
    void theCoverageChunkAloneDoesNotMentionEligibilityAtAll() {
        assertThat(check.containsBothFacts(coverageChunk, "covers", "eligible")).isFalse();
    }

    @Test
    void theEligibilityChunkAloneDoesNotMentionCoverageAtAll() {
        assertThat(check.containsBothFacts(eligibilityChunk, "covers", "eligible")).isFalse();
    }

    @Test
    void neitherChunkAloneContainsBothFactsTheCompoundQuestionNeeds() {
        boolean coverageChunkHasBoth = check.containsBothFacts(coverageChunk, "covers", "eligible");
        boolean eligibilityChunkHasBoth = check.containsBothFacts(eligibilityChunk, "covers", "eligible");

        assertThat(coverageChunkHasBoth || eligibilityChunkHasBoth)
                .as("answering the compound question requires combining both chunks -- neither one alone is enough")
                .isFalse();
    }
}
