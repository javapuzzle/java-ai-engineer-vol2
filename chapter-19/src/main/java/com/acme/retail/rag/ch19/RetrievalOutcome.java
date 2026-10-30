package com.acme.retail.rag.ch19;

/** One real customer interaction with retrieval -- the raw signal a monitor aggregates. */
public record RetrievalOutcome(String query, boolean gotNegativeFeedback) {
}
