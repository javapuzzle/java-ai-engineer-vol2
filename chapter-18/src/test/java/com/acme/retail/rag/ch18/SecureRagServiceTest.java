package com.acme.retail.rag.ch18;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SecureRagServiceTest {

    private final SecureRagService secureRagService = new SecureRagService(new AccessFilter());

    private final Document publicChunk = new Document(
            "return-policy", "Items may be returned within 30 days of purchase.",
            Map.of(AccessFilter.REQUIRED_SCOPE_METADATA_KEY, AccessScope.PUBLIC));
    private final Document seniorOnlyChunk = new Document(
            "pricing-override-policy",
            "Senior staff may authorize a refund above the standard limit without manager approval.",
            Map.of(AccessFilter.REQUIRED_SCOPE_METADATA_KEY, AccessScope.SENIOR_STAFF_ONLY));

    private final List<Document> allCandidates = List.of(publicChunk, seniorOnlyChunk);

    @Test
    void theRestrictedChunkNeverAppearsInTheFinalPromptForAJuniorAgent() {
        UserPermissions juniorAgent = new UserPermissions("agent-42", Set.of(AccessScope.PUBLIC));

        String prompt = secureRagService.buildPromptFor(
                juniorAgent, "Can I authorize a refund above the standard limit?", allCandidates);

        assertThat(prompt)
                .as("the restricted chunk was filtered out before the prompt was ever built -- "
                        + "it never had a chance to leak through generation")
                .doesNotContain(seniorOnlyChunk.getText());
        assertThat(prompt).contains(publicChunk.getText());
    }

    @Test
    void theSamePromptForSeniorStaffLegitimatelyIncludesTheRestrictedChunk() {
        UserPermissions seniorStaff = new UserPermissions(
                "senior-7", Set.of(AccessScope.PUBLIC, AccessScope.SENIOR_STAFF_ONLY));

        String prompt = secureRagService.buildPromptFor(
                seniorStaff, "Can I authorize a refund above the standard limit?", allCandidates);

        assertThat(prompt).contains(seniorOnlyChunk.getText());
    }
}
