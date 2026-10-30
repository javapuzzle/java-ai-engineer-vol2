package com.acme.retail.rag.ch14;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Answers exactly the compound question this chapter opens with: which
 * products satisfy TWO independent relationships at once (covered by a
 * plan, AND eligible for a program). This is a two-hop traversal expressed
 * as two one-hop lookups intersected -- the graph-native way to answer a
 * question that spans facts no single document chunk contains together.
 */
@Service
public class GraphQueryService {

    private final KnowledgeGraph graph;

    public GraphQueryService(KnowledgeGraph graph) {
        this.graph = graph;
    }

    public List<String> findProductsCoveredAndEligible(String planId, String tradeInProgramId) {
        List<String> coveredByPlan = graph.findObjects(planId, "COVERS");
        List<String> eligibleForProgram = graph.findSubjects("ELIGIBLE_FOR", tradeInProgramId);

        return coveredByPlan.stream()
                .filter(eligibleForProgram::contains)
                .toList();
    }
}
