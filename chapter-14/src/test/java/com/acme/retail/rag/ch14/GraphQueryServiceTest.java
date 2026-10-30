package com.acme.retail.rag.ch14;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GraphQueryServiceTest {

    private final List<Relationship> relationships = List.of(
            new Relationship("PlanA", "COVERS", "ProBlend500"),
            new Relationship("PlanA", "COVERS", "XR4400"),
            new Relationship("PlanA", "COVERS", "HP2210"),
            new Relationship("ProBlend500", "ELIGIBLE_FOR", "TradeInX"),
            new Relationship("HP2210", "ELIGIBLE_FOR", "TradeInX"),
            new Relationship("XR4400", "ELIGIBLE_FOR", "TradeInY")
    );

    private final GraphQueryService queryService = new GraphQueryService(new KnowledgeGraph(relationships));

    @Test
    void findsOnlyProductsSatisfyingBothTheCoverageAndEligibilityRelationships() {
        List<String> results = queryService.findProductsCoveredAndEligible("PlanA", "TradeInX");

        assertThat(results).containsExactlyInAnyOrder("ProBlend500", "HP2210");
    }

    @Test
    void excludesAProductThatIsCoveredButEligibleForADifferentProgram() {
        List<String> results = queryService.findProductsCoveredAndEligible("PlanA", "TradeInX");

        assertThat(results).doesNotContain("XR4400");
    }

    @Test
    void returnsEmptyForAPlanThatCoversNothing() {
        List<String> results = queryService.findProductsCoveredAndEligible("PlanZ", "TradeInX");

        assertThat(results).isEmpty();
    }
}
