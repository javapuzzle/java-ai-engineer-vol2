package com.acme.retail.rag.ch20;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectiveGradingServiceTest {

    private final CorrectiveGradingService gradingService = new CorrectiveGradingService();

    private final Document warrantyChunk = new Document(
            "warranty-policy", "Extended warranty covers manufacturing defects for two years from purchase date.", Map.of());
    private final Document shippingChunk = new Document(
            "shipping-info", "Standard shipping takes 3 to 5 business days within the continental US.", Map.of());

    @Test
    void aChunkSharingNoMeaningfulWordWithTheQuestionIsDropped() {
        List<Document> relevant = gradingService.gradeRelevant(
                "What does the warranty cover?", List.of(warrantyChunk, shippingChunk));

        assertThat(relevant).containsExactly(warrantyChunk);
    }

    @Test
    void aQuestionMatchingNoCandidateAtAllReturnsAnEmptyList() {
        List<Document> relevant = gradingService.gradeRelevant(
                "How do I track my order?", List.of(warrantyChunk, shippingChunk));

        assertThat(relevant).isEmpty();
    }
}
