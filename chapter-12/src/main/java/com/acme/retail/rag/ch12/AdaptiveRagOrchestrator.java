package com.acme.retail.rag.ch12;

import org.springframework.stereotype.Service;

/**
 * Turns a route into a decision a human can audit -- which strategy this
 * question would be sent to, and why. This class deliberately stops at the
 * decision: actually querying {@code StructuredKnowledgeBase} (Chapter 1),
 * running the RAG pipeline (Chapter 8), or calling a live order-tracking
 * API (not built in this volume) is each strategy's own concern, not the
 * router's.
 */
@Service
public class AdaptiveRagOrchestrator {

    private final AdaptiveQueryRouter router;

    public AdaptiveRagOrchestrator(AdaptiveQueryRouter router) {
        this.router = router;
    }

    public RoutingDecision decide(String question) {
        QueryRoute route = router.route(question);

        String rationale = switch (route) {
            case STRUCTURED_FACT_LOOKUP ->
                    "Matches a stock/price phrase -- settle this with a direct row lookup (Chapter 1), no retrieval needed.";
            case DOCUMENT_RAG ->
                    "No structured-fact or live-system phrase matched -- run the document retrieval pipeline (Chapter 8).";
            case EXTERNAL_TOOL_CALL ->
                    "Matches an order-tracking phrase -- no document or database row can answer this; "
                            + "it needs a live external system call, not built in this volume.";
        };

        return new RoutingDecision(route, rationale);
    }
}
