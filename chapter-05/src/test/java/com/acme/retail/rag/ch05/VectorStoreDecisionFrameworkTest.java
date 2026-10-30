package com.acme.retail.rag.ch05;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VectorStoreDecisionFrameworkTest {

    private final VectorStoreDecisionFramework framework = new VectorStoreDecisionFramework();

    private final VectorStoreCandidate vendorManagedFlagship = new VectorStoreCandidate(
            "vendor-managed-flagship", true, true, OverheadLevel.LOW, OverheadLevel.HIGH);
    private final VectorStoreCandidate selfHostedOss = new VectorStoreCandidate(
            "self-hosted-oss", true, true, OverheadLevel.HIGH, OverheadLevel.MEDIUM);
    private final VectorStoreCandidate pgvectorExtension = new VectorStoreCandidate(
            "postgres-pgvector-extension", false, true, OverheadLevel.MEDIUM, OverheadLevel.LOW);
    private final VectorStoreCandidate budgetCloudVector = new VectorStoreCandidate(
            "budget-cloud-vector", false, false, OverheadLevel.LOW, OverheadLevel.LOW);

    private final List<VectorStoreCandidate> allCandidates =
            List.of(vendorManagedFlagship, selfHostedOss, pgvectorExtension, budgetCloudVector);

    @Test
    void whenHybridSearchIsNotRequiredButMetadataFilteringIsThePgvectorExtensionWinsOnOverheadAndCost() {
        AcmeVectorStoreRequirements requirements = new AcmeVectorStoreRequirements(
                false, true, OverheadLevel.MEDIUM, OverheadLevel.MEDIUM);

        List<VectorStoreCandidate> result = framework.recommend(allCandidates, requirements);

        assertThat(result).containsExactly(pgvectorExtension);
    }

    @Test
    void whenHybridSearchIsRequiredThePgvectorExtensionAndBudgetOptionAreBothDisqualified() {
        AcmeVectorStoreRequirements requirements = new AcmeVectorStoreRequirements(
                true, true, OverheadLevel.HIGH, OverheadLevel.HIGH);

        List<VectorStoreCandidate> result = framework.recommend(allCandidates, requirements);

        assertThat(result).containsExactly(vendorManagedFlagship, selfHostedOss);
    }

    @Test
    void aStrictLowOverheadAndLowCostCeilingLeavesOnlyTheBudgetOptionWhenNothingElseIsRequired() {
        AcmeVectorStoreRequirements requirements = new AcmeVectorStoreRequirements(
                false, false, OverheadLevel.LOW, OverheadLevel.LOW);

        List<VectorStoreCandidate> result = framework.recommend(allCandidates, requirements);

        assertThat(result).containsExactly(budgetCloudVector);
    }

    @Test
    void requiringHybridSearchAndMetadataFilteringUnderAStrictCostCeilingLeavesNoCandidate() {
        AcmeVectorStoreRequirements impossible = new AcmeVectorStoreRequirements(
                true, true, OverheadLevel.LOW, OverheadLevel.LOW);

        List<VectorStoreCandidate> result = framework.recommend(allCandidates, impossible);

        assertThat(result).isEmpty();
    }
}
