package com.acme.retail.rag.ch12;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Classifies a question by which retrieval strategy could actually answer
 * it -- the same discipline Chapter 1's {@code CustomerQuestionClassifier}
 * used for two categories, extended here to three. A production router
 * typically uses an LLM call for this classification; this chapter's
 * version uses a small, explicit keyword list so the routing logic itself
 * is deterministic and testable.
 */
@Service
public class AdaptiveQueryRouter {

    private static final List<String> STRUCTURED_FACT_PHRASES = List.of(
            "in stock", "available", "price", "cost", "how much"
    );

    private static final List<String> EXTERNAL_TOOL_PHRASES = List.of(
            "where is my order", "order status", "track my order", "tracking number"
    );

    public QueryRoute route(String question) {
        String normalized = question.toLowerCase(Locale.ROOT);

        if (EXTERNAL_TOOL_PHRASES.stream().anyMatch(normalized::contains)) {
            return QueryRoute.EXTERNAL_TOOL_CALL;
        }
        if (STRUCTURED_FACT_PHRASES.stream().anyMatch(normalized::contains)) {
            return QueryRoute.STRUCTURED_FACT_LOOKUP;
        }
        return QueryRoute.DOCUMENT_RAG;
    }
}
