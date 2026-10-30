package com.acme.retail.rag.ch20;

import org.springframework.ai.document.Document;

import java.util.List;

/**
 * The one contract every version of the assistant satisfies -- the secure
 * pipeline itself, and both the fixed and the rejected caching wrappers
 * around it. Giving {@link KnowledgeAssistantReleaseGate} this interface,
 * rather than a concrete class, is what lets the same gate evaluate any of
 * them identically.
 */
public interface KnowledgeAssistant {

    KnowledgeAssistantAnswer answer(String question, UserPermissions user, List<Document> candidatePool);
}
