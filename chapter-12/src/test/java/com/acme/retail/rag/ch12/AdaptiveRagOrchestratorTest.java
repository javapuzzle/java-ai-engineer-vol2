package com.acme.retail.rag.ch12;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdaptiveRagOrchestratorTest {

    private final AdaptiveRagOrchestrator orchestrator = new AdaptiveRagOrchestrator(new AdaptiveQueryRouter());

    @Test
    void explainsWhyAStockQuestionNeedsNoRetrievalAtAll() {
        RoutingDecision decision = orchestrator.decide("Is this in stock?");

        assertThat(decision.route()).isEqualTo(QueryRoute.STRUCTURED_FACT_LOOKUP);
        assertThat(decision.rationale()).contains("row lookup");
    }

    @Test
    void explainsWhyAnOrderTrackingQuestionNeedsALiveSystemNotADocument() {
        RoutingDecision decision = orchestrator.decide("Where is my order?");

        assertThat(decision.route()).isEqualTo(QueryRoute.EXTERNAL_TOOL_CALL);
        assertThat(decision.rationale()).contains("live external system");
    }

    @Test
    void explainsWhyAPolicyQuestionFallsThroughToDocumentRetrieval() {
        RoutingDecision decision = orchestrator.decide("What's covered under the extended warranty?");

        assertThat(decision.route()).isEqualTo(QueryRoute.DOCUMENT_RAG);
        assertThat(decision.rationale()).contains("retrieval pipeline");
    }
}
