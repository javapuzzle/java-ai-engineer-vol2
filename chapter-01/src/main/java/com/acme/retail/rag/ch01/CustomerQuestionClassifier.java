package com.acme.retail.rag.ch01;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Classifies a customer question by what kind of knowledge could ever
 * answer it -- not by how confident an LLM sounds when it tries.
 *
 * <p>The keyword list below is deliberately small and honest: it only
 * recognizes the handful of fields a {@link ProductFact} row actually
 * has (stock, price). Everything else -- warranty terms, return windows,
 * dishwasher-safety, anything that lives in a document -- falls through to
 * {@link QuestionType#DOCUMENT_KNOWLEDGE} by default, because as of this
 * chapter, nothing in this codebase indexes documents at all.
 */
@Service
public class CustomerQuestionClassifier {

    private static final List<String> STRUCTURED_FACT_PHRASES = List.of(
            "in stock", "available", "price", "cost", "how much"
    );

    public QuestionType classify(String question) {
        String normalized = question.toLowerCase(Locale.ROOT);
        boolean matchesStructuredField = STRUCTURED_FACT_PHRASES.stream()
                .anyMatch(normalized::contains);

        return matchesStructuredField ? QuestionType.STRUCTURED_FACT : QuestionType.DOCUMENT_KNOWLEDGE;
    }
}
