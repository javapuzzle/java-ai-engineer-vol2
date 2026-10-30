package com.acme.retail.rag.ch01;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerQuestionClassifierTest {

    private final CustomerQuestionClassifier classifier = new CustomerQuestionClassifier();

    @Test
    void classifiesStockQuestionAsStructuredFact() {
        assertThat(classifier.classify("Is the TrailBlend 3000 in stock?"))
                .isEqualTo(QuestionType.STRUCTURED_FACT);
    }

    @Test
    void classifiesPriceQuestionAsStructuredFact() {
        assertThat(classifier.classify("How much does the XR-4400 cost?"))
                .isEqualTo(QuestionType.STRUCTURED_FACT);
    }

    @Test
    void classifiesDishwasherSafetyQuestionAsDocumentKnowledge() {
        assertThat(classifier.classify("Is the TrailBlend 3000 dishwasher safe?"))
                .isEqualTo(QuestionType.DOCUMENT_KNOWLEDGE);
    }

    @Test
    void classifiesReturnPolicyQuestionAsDocumentKnowledge() {
        assertThat(classifier.classify("Can I return an opened pair of headphones?"))
                .isEqualTo(QuestionType.DOCUMENT_KNOWLEDGE);
    }

    @Test
    void classifiesWarrantyQuestionAsDocumentKnowledge() {
        assertThat(classifier.classify("What's covered under the extended warranty on the XR-4400?"))
                .isEqualTo(QuestionType.DOCUMENT_KNOWLEDGE);
    }
}
