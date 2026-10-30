package com.acme.retail.rag.ch01;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StructuredAnswerServiceTest {

    private final StructuredAnswerService answerService = new StructuredAnswerService(
            new StructuredKnowledgeBase(), new CustomerQuestionClassifier());

    @Test
    void answersAStockQuestionForAKnownSku() {
        assertThat(answerService.answer("XR-4400", "Is the TrailBlend 3000 in stock?"))
                .contains("TrailBlend 3000 Blender is in stock.");
    }

    @Test
    void answersAnOutOfStockQuestionHonestly() {
        assertThat(answerService.answer("HP-2210", "Is this available?"))
                .contains("Acme Wireless Headphones is currently out of stock.");
    }

    @Test
    void cannotAnswerAStructuredQuestionForAnUnknownSku() {
        assertThat(answerService.answer("NOT-A-REAL-SKU", "Is it in stock?")).isEmpty();
    }

    @Test
    void cannotAnswerADishwasherSafetyQuestionEvenForAKnownSku() {
        assertThat(answerService.answer("XR-4400", "Is the TrailBlend 3000 dishwasher safe?"))
                .isEmpty();
    }

    @Test
    void cannotAnswerAReturnPolicyQuestionEvenForAKnownSku() {
        assertThat(answerService.answer("HP-2210", "Can I return this if I already opened it?"))
                .isEmpty();
    }

    @Test
    void cannotAnswerAWarrantyQuestionEvenForAKnownSku() {
        assertThat(answerService.answer("XR-4400", "What's covered under the extended warranty on this?"))
                .isEmpty();
    }
}
