package com.acme.retail.rag.ch20;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeAssistantRouterTest {

    private final KnowledgeAssistantRouter router = new KnowledgeAssistantRouter();

    @Test
    void aStockQuestionRoutesToStructuredFactLookup() {
        assertThat(router.route("Is the ProBlend500 in stock?")).isEqualTo(QueryRoute.STRUCTURED_FACT_LOOKUP);
    }

    @Test
    void anOrderTrackingQuestionRoutesToExternalToolCall() {
        assertThat(router.route("Where is my order?")).isEqualTo(QueryRoute.EXTERNAL_TOOL_CALL);
    }

    @Test
    void aPolicyQuestionRoutesToDocumentRag() {
        assertThat(router.route("Can I return an opened blender?")).isEqualTo(QueryRoute.DOCUMENT_RAG);
    }
}
