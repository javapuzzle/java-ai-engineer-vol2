package com.acme.retail.rag.ch01;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The full extent of what Acme Retail's assistant can answer, as of the
 * start of Volume 2: exactly the questions a {@link ProductFact} row
 * settles, and nothing else. This class has no fallback, no retrieval,
 * and no document store -- that gap is the rest of this volume.
 */
@Service
public class StructuredAnswerService {

    private final StructuredKnowledgeBase knowledgeBase;
    private final CustomerQuestionClassifier classifier;

    public StructuredAnswerService(StructuredKnowledgeBase knowledgeBase,
                                    CustomerQuestionClassifier classifier) {
        this.knowledgeBase = knowledgeBase;
        this.classifier = classifier;
    }

    /**
     * Returns an answer only when the question is a {@link QuestionType#STRUCTURED_FACT}
     * AND a matching row exists. Every {@link QuestionType#DOCUMENT_KNOWLEDGE}
     * question returns empty, no matter which SKU it's asked about --
     * there is nothing in this class capable of reading a document yet.
     */
    public Optional<String> answer(String sku, String question) {
        if (classifier.classify(question) != QuestionType.STRUCTURED_FACT) {
            return Optional.empty();
        }

        return knowledgeBase.findBySku(sku)
                .map(fact -> fact.inStock()
                        ? fact.name() + " is in stock."
                        : fact.name() + " is currently out of stock.");
    }
}
