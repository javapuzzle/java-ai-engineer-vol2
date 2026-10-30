package com.acme.retail.rag.ch18;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AccessFilterTest {

    private final AccessFilter accessFilter = new AccessFilter();

    private final Document publicChunk = new Document(
            "return-policy", "Items may be returned within 30 days of purchase.",
            Map.of(AccessFilter.REQUIRED_SCOPE_METADATA_KEY, AccessScope.PUBLIC));
    private final Document internalChunk = new Document(
            "support-runbook", "For repeated warranty claims, escalate to the vendor liaison.",
            Map.of(AccessFilter.REQUIRED_SCOPE_METADATA_KEY, AccessScope.INTERNAL_SUPPORT));
    private final Document seniorOnlyChunk = new Document(
            "pricing-override-policy", "Senior staff may authorize a refund above the standard limit without manager approval.",
            Map.of(AccessFilter.REQUIRED_SCOPE_METADATA_KEY, AccessScope.SENIOR_STAFF_ONLY));

    private final List<Document> allCandidates = List.of(publicChunk, internalChunk, seniorOnlyChunk);

    @Test
    void aJuniorSupportAgentNeverReceivesTheSeniorOnlyChunk() {
        UserPermissions juniorAgent = new UserPermissions(
                "agent-42", Set.of(AccessScope.PUBLIC, AccessScope.INTERNAL_SUPPORT));

        List<Document> result = accessFilter.filterByPermission(allCandidates, juniorAgent);

        assertThat(result).containsExactlyInAnyOrder(publicChunk, internalChunk);
        assertThat(result).doesNotContain(seniorOnlyChunk);
    }

    @Test
    void seniorStaffReceiveEveryChunkTheyAreClearedFor() {
        UserPermissions seniorStaff = new UserPermissions(
                "senior-7", Set.of(AccessScope.PUBLIC, AccessScope.INTERNAL_SUPPORT, AccessScope.SENIOR_STAFF_ONLY));

        List<Document> result = accessFilter.filterByPermission(allCandidates, seniorStaff);

        assertThat(result).containsExactlyInAnyOrder(publicChunk, internalChunk, seniorOnlyChunk);
    }

    @Test
    void anAnonymousCustomerOnlyReceivesPublicChunks() {
        UserPermissions anonymousCustomer = new UserPermissions("guest", Set.of(AccessScope.PUBLIC));

        List<Document> result = accessFilter.filterByPermission(allCandidates, anonymousCustomer);

        assertThat(result).containsExactly(publicChunk);
    }
}
