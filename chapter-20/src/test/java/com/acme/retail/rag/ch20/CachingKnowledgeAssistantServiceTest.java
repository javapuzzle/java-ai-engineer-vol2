package com.acme.retail.rag.ch20;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CachingKnowledgeAssistantServiceTest {

    private static final String QUESTION = "Can I get a refund above the standard limit?";

    private final Document publicChunk = new Document(
            "return-policy",
            "Customers may request a refund through the standard returns process within 30 days.",
            Map.of(AccessFilter.REQUIRED_SCOPE_METADATA_KEY, AccessScope.PUBLIC));
    private final Document seniorOnlyChunk = new Document(
            "pricing-override-policy",
            "Senior staff may authorize a refund above the standard limit without manager approval.",
            Map.of(AccessFilter.REQUIRED_SCOPE_METADATA_KEY, AccessScope.SENIOR_STAFF_ONLY));

    private final List<Document> candidatePool = List.of(publicChunk, seniorOnlyChunk);

    private final UserPermissions juniorAgent = new UserPermissions("agent-42", Set.of(AccessScope.PUBLIC));
    private final UserPermissions seniorStaff = new UserPermissions(
            "senior-7", Set.of(AccessScope.PUBLIC, AccessScope.SENIOR_STAFF_ONLY));
    private final UserPermissions anotherSeniorStaff = new UserPermissions(
            "senior-9", Set.of(AccessScope.PUBLIC, AccessScope.SENIOR_STAFF_ONLY));

    private final CachingKnowledgeAssistantService cache = new CachingKnowledgeAssistantService(
            new KnowledgeAssistantPipeline(
                    new KnowledgeAssistantRouter(), new AccessFilter(), new CorrectiveGradingService(),
                    new KnowledgeAssistantPromptBuilder()));

    @Test
    void aJuniorAgentNeverReceivesTheSeniorStaffMembersCachedAnswer() {
        KnowledgeAssistantAnswer seniorAnswer = cache.answer(QUESTION, seniorStaff, candidatePool);
        assertThat(seniorAnswer.prompt()).contains(seniorOnlyChunk.getText());

        KnowledgeAssistantAnswer juniorAnswer = cache.answer(QUESTION, juniorAgent, candidatePool);

        assertThat(juniorAnswer.prompt())
                .as("the cache key includes the requester's permission-scope signature -- "
                        + "a junior agent can never hit a senior staff member's cache entry")
                .doesNotContain(seniorOnlyChunk.getText());
        assertThat(cache.cacheSize())
                .as("two different scope signatures for the same question means two separate cache entries")
                .isEqualTo(2);
    }

    @Test
    void twoDifferentUsersWithTheIdenticalPermissionSetShareOneCacheEntry() {
        cache.answer(QUESTION, seniorStaff, candidatePool);
        cache.answer(QUESTION, anotherSeniorStaff, candidatePool);

        assertThat(cache.cacheSize())
                .as("caching is scoped to what's actually security-relevant -- the granted scopes -- "
                        + "not needlessly fragmented by raw user identity")
                .isEqualTo(1);
    }
}
