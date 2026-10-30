package com.acme.retail.rag.ch03;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Filters candidate embedding models against Acme Retail's stated
 * requirements, then ranks survivors cheapest-first. The point isn't that
 * cheapest is always right -- it's that ranking happens only after
 * disqualifying anything that fails a hard requirement, instead of ranking
 * everything by cost or popularity first and hoping the winner happens to
 * fit.
 */
@Service
public class EmbeddingModelSelector {

    public List<EmbeddingModelCandidate> filterAndRank(
            List<EmbeddingModelCandidate> candidates,
            EmbeddingRequirements requirements) {

        return candidates.stream()
                .filter(candidate -> candidate.dimensions() <= requirements.maxDimensions())
                .filter(candidate -> !requirements.requireDomainTuned() || candidate.domainTuned())
                .filter(candidate -> candidate.costPerMillionTokensCents() <= requirements.maxCostPerMillionTokensCents())
                .sorted(Comparator.comparingInt(EmbeddingModelCandidate::costPerMillionTokensCents))
                .toList();
    }
}
