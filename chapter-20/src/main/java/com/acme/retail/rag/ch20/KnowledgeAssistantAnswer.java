package com.acme.retail.rag.ch20;

/**
 * One question's outcome. {@code route} is set even when the pipeline
 * never touches retrieval at all -- Chapter 12's discipline of stopping at
 * the routing decision for strategies this volume doesn't implement.
 */
public record KnowledgeAssistantAnswer(QueryRoute route, boolean answerable, String prompt) {

    public static KnowledgeAssistantAnswer routedElsewhere(QueryRoute route) {
        return new KnowledgeAssistantAnswer(route, false, null);
    }

    public static KnowledgeAssistantAnswer unanswerable() {
        return new KnowledgeAssistantAnswer(QueryRoute.DOCUMENT_RAG, false, null);
    }

    public static KnowledgeAssistantAnswer answerable(String prompt) {
        return new KnowledgeAssistantAnswer(QueryRoute.DOCUMENT_RAG, true, prompt);
    }
}
