package com.acme.retail.rag.ch20;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeAssistantPipelineTest {

    private static final String QUESTION = "Can I get a refund above the standard limit?";

    private final Document publicChunk = new Document(
            "return-policy",
            "Customers may request a refund through the standard returns process within 30 days.",
            Map.of(AccessFilter.REQUIRED_SCOPE_METADATA_KEY, AccessScope.PUBLIC));
    private final Document seniorOnlyChunk = new Document(
            "pricing-override-policy",
            "Senior staff may authorize a refund above the standard limit without manager approval.",
            Map.of(AccessFilter.REQUIRED_SCOPE_METADATA_KEY, AccessScope.SENIOR_STAFF_ONLY));

    private final UserPermissions juniorAgent = new UserPermissions("agent-42", Set.of(AccessScope.PUBLIC));
    private final UserPermissions seniorStaff = new UserPermissions(
            "senior-7", Set.of(AccessScope.PUBLIC, AccessScope.SENIOR_STAFF_ONLY));

    private final KnowledgeAssistantPipeline pipeline = new KnowledgeAssistantPipeline(
            new KnowledgeAssistantRouter(), new AccessFilter(), new CorrectiveGradingService(),
            new KnowledgeAssistantPromptBuilder());

    @Test
    void aStockQuestionNeverReachesRetrievalAtAll() {
        KnowledgeAssistantAnswer answer = pipeline.answer(
                "Is the ProBlend500 in stock?", juniorAgent, List.of(publicChunk, seniorOnlyChunk));

        assertThat(answer.route()).isEqualTo(QueryRoute.STRUCTURED_FACT_LOOKUP);
        assertThat(answer.answerable()).isFalse();
        assertThat(answer.prompt()).isNull();
    }

    @Test
    void aJuniorAgentWithOnlyARestrictedCandidateGetsAnHonestUnanswerableResult() {
        KnowledgeAssistantAnswer answer = pipeline.answer(QUESTION, juniorAgent, List.of(seniorOnlyChunk));

        assertThat(answer.route()).isEqualTo(QueryRoute.DOCUMENT_RAG);
        assertThat(answer.answerable())
                .as("the only matching candidate was filtered out before grading ever ran")
                .isFalse();
        assertThat(answer.prompt()).isNull();
    }

    @Test
    void theFinalPromptNeverContainsARestrictedChunkEvenWhenItWouldHaveGradedRelevant() {
        KnowledgeAssistantAnswer answer = pipeline.answer(
                QUESTION, juniorAgent, List.of(publicChunk, seniorOnlyChunk));

        assertThat(answer.answerable()).isTrue();
        assertThat(answer.prompt()).contains(publicChunk.getText());
        assertThat(answer.prompt())
                .as("seniorOnlyChunk shares 'refund', 'standard', and 'limit' with the question -- "
                        + "it would grade relevant if it ever reached grading, but AccessFilter runs first")
                .doesNotContain(seniorOnlyChunk.getText());
    }

    @Test
    void seniorStaffAskingTheSameQuestionLegitimatelyReceivesBothChunks() {
        KnowledgeAssistantAnswer answer = pipeline.answer(
                QUESTION, seniorStaff, List.of(publicChunk, seniorOnlyChunk));

        assertThat(answer.answerable()).isTrue();
        assertThat(answer.prompt()).contains(publicChunk.getText());
        assertThat(answer.prompt()).contains(seniorOnlyChunk.getText());
    }
}
