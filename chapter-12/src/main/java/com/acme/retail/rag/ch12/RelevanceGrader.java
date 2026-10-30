package com.acme.retail.rag.ch12;

/**
 * The relevance-grading seam. Production Corrective RAG typically uses an
 * LLM call here -- "does this passage actually answer this question, yes
 * or no" -- prompted as a narrow, binary judgment. This chapter's
 * implementation uses a deterministic, testable heuristic instead, the
 * same deliberate simplification prior chapters used for embeddings and
 * reranking.
 */
public interface RelevanceGrader {

    RelevanceGrade grade(String query, String chunkText);
}
