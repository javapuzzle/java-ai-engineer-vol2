package com.acme.retail.rag.ch20;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Classifies a question by which strategy could actually answer it --
 * Chapter 12's {@code AdaptiveQueryRouter}, unchanged in shape, reused here
 * as the capstone's front door. A question about stock or price never
 * needs document retrieval at all; a question about an order's live
 * status needs a system this volume never builds; everything else is
 * exactly what the rest of this pipeline exists for.
 */
@Service
public class KnowledgeAssistantRouter {

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
