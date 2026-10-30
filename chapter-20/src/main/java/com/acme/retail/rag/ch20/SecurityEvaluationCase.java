package com.acme.retail.rag.ch20;

import org.springframework.ai.document.Document;

import java.util.List;

/**
 * One row of the capstone's security release-gate dataset: a question, the
 * specific requester asking it, and the full unfiltered candidate pool
 * that requester's query would retrieve against. What counts as a
 * "correct" outcome is computed by the gate itself, from {@code user} and
 * {@code candidatePool} -- never hand-coded per case -- so the same case
 * set stays valid no matter which {@link KnowledgeAssistant} implementation
 * it's run against.
 */
public record SecurityEvaluationCase(String question, UserPermissions user, List<Document> candidatePool) {
}
