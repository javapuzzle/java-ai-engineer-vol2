package com.acme.retail.rag.ch14;

/**
 * One extracted fact: subject --predicate--&gt; object. A real system
 * populates these via an LLM extraction pass over documents; this chapter
 * hand-authors them, the same deliberate simplification used for
 * embeddings throughout this volume -- the point here is what the graph
 * makes possible once facts exist as relationships, not how those facts
 * get extracted.
 */
public record Relationship(String subject, String predicate, String object) {
}
