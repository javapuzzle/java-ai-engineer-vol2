package com.acme.retail.rag.ch20;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reproduces, live, the exact shape Sarah's team proposed during the
 * capstone design review: caching an assistant's answer by question text
 * alone. This test's assertion proves the flaw is real and reproducible --
 * the same rhetorical device Chapter 10 used to first prove embedding-only
 * ranking's mistake before correcting it.
 */
class QuestionOnlyAnswerCacheTest {

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
    void aQuestionOnlyCacheLeaksASeniorStaffAnswerToALaterJuniorAgent() {
        QuestionOnlyAnswerCache cache = new QuestionOnlyAnswerCache(pipeline);
        List<Document> candidatePool = List.of(publicChunk, seniorOnlyChunk);

        KnowledgeAssistantAnswer seniorAnswer = cache.answer(QUESTION, seniorStaff, candidatePool);
        assertThat(seniorAnswer.prompt()).contains(seniorOnlyChunk.getText());

        KnowledgeAssistantAnswer juniorAnswer = cache.answer(QUESTION, juniorAgent, candidatePool);

        assertThat(juniorAnswer.prompt())
                .as("the cache key ignores who is asking -- it returned the senior staff member's "
                        + "already-computed answer verbatim, without AccessFilter ever running for the junior agent")
                .contains(seniorOnlyChunk.getText());
    }
}
