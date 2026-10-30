package com.acme.retail.rag.ch03;

/**
 * What Acme Retail actually needs from an embedding model, stated as a
 * checkable requirement rather than a vague preference. {@code maxDimensions}
 * exists because Chapter 5's chosen vector store (or a cost budget) may cap
 * how large a vector is worth storing per chunk.
 */
public record EmbeddingRequirements(
        int maxDimensions,
        boolean requireDomainTuned,
        int maxCostPerMillionTokensCents
) {
}
