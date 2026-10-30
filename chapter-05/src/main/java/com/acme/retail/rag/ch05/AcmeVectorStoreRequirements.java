package com.acme.retail.rag.ch05;

/**
 * Acme Retail's stated requirements for a vector store, as concrete,
 * checkable properties -- not "let's just use whatever's popular."
 */
public record AcmeVectorStoreRequirements(
        boolean requireHybridSearch,
        boolean requireMetadataFiltering,
        OverheadLevel maxOperationalOverhead,
        OverheadLevel maxCostTier
) {
}
