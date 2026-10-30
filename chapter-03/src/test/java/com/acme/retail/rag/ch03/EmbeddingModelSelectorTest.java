package com.acme.retail.rag.ch03;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmbeddingModelSelectorTest {

    private final EmbeddingModelSelector selector = new EmbeddingModelSelector();

    private final EmbeddingModelCandidate generalSmall =
            new EmbeddingModelCandidate("general-small", 384, false, 2, 8192);
    private final EmbeddingModelCandidate generalLarge =
            new EmbeddingModelCandidate("general-large", 1536, false, 13, 8192);
    private final EmbeddingModelCandidate retailTunedSmall =
            new EmbeddingModelCandidate("retail-tuned-small", 512, true, 9, 4096);
    private final EmbeddingModelCandidate flagshipLarge =
            new EmbeddingModelCandidate("flagship-large", 3072, false, 20, 8192);

    private final List<EmbeddingModelCandidate> allCandidates =
            List.of(generalSmall, generalLarge, retailTunedSmall, flagshipLarge);

    @Test
    void withNoHardConstraintsRanksEverythingCheapestFirst() {
        EmbeddingRequirements permissive = new EmbeddingRequirements(4096, false, 100);

        List<EmbeddingModelCandidate> result = selector.filterAndRank(allCandidates, permissive);

        assertThat(result).containsExactly(generalSmall, retailTunedSmall, generalLarge, flagshipLarge);
    }

    @Test
    void requiringDomainTuningExcludesEveryGeneralPurposeModelEvenCheaperOnes() {
        EmbeddingRequirements requireDomainFit = new EmbeddingRequirements(4096, true, 100);

        List<EmbeddingModelCandidate> result = selector.filterAndRank(allCandidates, requireDomainFit);

        assertThat(result).containsExactly(retailTunedSmall);
    }

    @Test
    void aCostCeilingExcludesModelsAboveItRegardlessOfQuality() {
        EmbeddingRequirements costConstrained = new EmbeddingRequirements(4096, false, 10);

        List<EmbeddingModelCandidate> result = selector.filterAndRank(allCandidates, costConstrained);

        assertThat(result).containsExactly(generalSmall, retailTunedSmall);
    }

    @Test
    void aDimensionCeilingExcludesModelsThatWouldExceedTheVectorStoresLimit() {
        EmbeddingRequirements dimensionConstrained = new EmbeddingRequirements(1000, false, 100);

        List<EmbeddingModelCandidate> result = selector.filterAndRank(allCandidates, dimensionConstrained);

        assertThat(result).containsExactly(generalSmall, retailTunedSmall);
    }

    @Test
    void combiningConstraintsCanLegitimatelyLeaveNoCandidate() {
        EmbeddingRequirements impossible = new EmbeddingRequirements(4096, true, 5);

        List<EmbeddingModelCandidate> result = selector.filterAndRank(allCandidates, impossible);

        assertThat(result).isEmpty();
    }
}
