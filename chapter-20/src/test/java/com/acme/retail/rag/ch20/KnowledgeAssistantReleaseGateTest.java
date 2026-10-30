package com.acme.retail.rag.ch20;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The same case set, run against both the rejected and the fixed
 * assistant, proves the release gate itself is the thing catching the
 * regression -- not a person eyeballing the two answers and feeling
 * reassured, the exact distinction Volume 1 Chapter 20 drew between "the
 * fix is deployed" and "the fix is verified."
 */
class KnowledgeAssistantReleaseGateTest {

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

    private final List<SecurityEvaluationCase> incidentCases = List.of(
            new SecurityEvaluationCase(QUESTION, seniorStaff, candidatePool),
            new SecurityEvaluationCase(QUESTION, juniorAgent, candidatePool));

    private final KnowledgeAssistantPipeline pipeline = new KnowledgeAssistantPipeline(
            new KnowledgeAssistantRouter(), new AccessFilter(), new CorrectiveGradingService(),
            new KnowledgeAssistantPromptBuilder());

    private final KnowledgeAssistantReleaseGate gate = new KnowledgeAssistantReleaseGate();

    @Test
    void theRejectedQuestionOnlyCacheFailsTheSecurityReleaseGate() {
        QuestionOnlyAnswerCache rejectedCache = new QuestionOnlyAnswerCache(pipeline);

        double complianceRate = gate.securityComplianceRate(incidentCases, rejectedCache);

        assertThat(complianceRate)
                .as("the second case -- the junior agent's request -- receives the senior staff "
                        + "member's cached, unfiltered-for-them answer")
                .isLessThan(1.0);
        assertThat(gate.meetsReleaseGate(complianceRate)).isFalse();
    }

    @Test
    void theFixedPermissionAwareCachePassesTheSecurityReleaseGate() {
        CachingKnowledgeAssistantService fixedCache = new CachingKnowledgeAssistantService(pipeline);

        double complianceRate = gate.securityComplianceRate(incidentCases, fixedCache);

        assertThat(complianceRate).isEqualTo(1.0);
        assertThat(gate.meetsReleaseGate(complianceRate)).isTrue();
    }
}
