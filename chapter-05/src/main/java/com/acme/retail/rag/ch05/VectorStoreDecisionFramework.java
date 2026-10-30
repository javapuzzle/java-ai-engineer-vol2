package com.acme.retail.rag.ch05;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Filters vector store candidates against Acme's hard requirements, then
 * ranks survivors by combined operational overhead and cost -- lower is
 * better on both. This is a decision framework, not a benchmark leaderboard:
 * a candidate that fails a required capability is disqualified before its
 * overhead or cost is ever considered.
 */
@Service
public class VectorStoreDecisionFramework {

    public List<VectorStoreCandidate> recommend(
            List<VectorStoreCandidate> candidates,
            AcmeVectorStoreRequirements requirements) {

        return candidates.stream()
                .filter(candidate -> !requirements.requireHybridSearch() || candidate.supportsHybridSearch())
                .filter(candidate -> !requirements.requireMetadataFiltering() || candidate.supportsMetadataFiltering())
                .filter(candidate -> candidate.operationalOverhead().ordinal() <= requirements.maxOperationalOverhead().ordinal())
                .filter(candidate -> candidate.costTier().ordinal() <= requirements.maxCostTier().ordinal())
                .sorted(Comparator.comparingInt(this::combinedScore))
                .toList();
    }

    private int combinedScore(VectorStoreCandidate candidate) {
        return candidate.operationalOverhead().ordinal() + candidate.costTier().ordinal();
    }
}
