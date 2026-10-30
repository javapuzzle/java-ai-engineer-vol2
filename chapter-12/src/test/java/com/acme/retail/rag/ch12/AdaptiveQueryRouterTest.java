package com.acme.retail.rag.ch12;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdaptiveQueryRouterTest {

    private final AdaptiveQueryRouter router = new AdaptiveQueryRouter();

    @Test
    void routesAStockQuestionToStructuredFactLookup() {
        assertThat(router.route("Is the TrailBlend 3000 in stock?")).isEqualTo(QueryRoute.STRUCTURED_FACT_LOOKUP);
    }

    @Test
    void routesAPolicyQuestionToDocumentRag() {
        assertThat(router.route("What is your return policy for opened electronics?"))
                .isEqualTo(QueryRoute.DOCUMENT_RAG);
    }

    @Test
    void routesAnOrderTrackingQuestionToExternalToolCall() {
        assertThat(router.route("Where is my order? I need the tracking number."))
                .isEqualTo(QueryRoute.EXTERNAL_TOOL_CALL);
    }

    @Test
    void anOrderTrackingPhraseTakesPriorityOverAnIncidentalStockMention() {
        String question = "Where is my order status? I ordered it because it was in stock last week.";

        assertThat(router.route(question)).isEqualTo(QueryRoute.EXTERNAL_TOOL_CALL);
    }
}
