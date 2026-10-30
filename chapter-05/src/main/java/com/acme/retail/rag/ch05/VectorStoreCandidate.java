package com.acme.retail.rag.ch05;

public record VectorStoreCandidate(
        String name,
        boolean supportsHybridSearch,
        boolean supportsMetadataFiltering,
        OverheadLevel operationalOverhead,
        OverheadLevel costTier
) {
}
