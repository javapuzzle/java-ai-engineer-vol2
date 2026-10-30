package com.acme.retail.rag.ch03;

/**
 * A candidate embedding model, described only by the properties that
 * actually matter for choosing one: how many numbers it outputs per
 * chunk, whether it was tuned on anything like Acme's retail/support
 * domain, and what it costs to run.
 */
public record EmbeddingModelCandidate(
        String name,
        int dimensions,
        boolean domainTuned,
        int costPerMillionTokensCents,
        int maxInputTokens
) {
}
