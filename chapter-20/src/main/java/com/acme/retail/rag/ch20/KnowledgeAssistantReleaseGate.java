package com.acme.retail.rag.ch20;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The same diagnose/verify/recover discipline Volume 1 Chapter 20 used for
 * a resilience incident, and Chapter 17 used for a quality regression,
 * applied here to a security regression: a fix is never trusted because it
 * "should" work, only because a fixed, repeatable case set proves it.
 *
 * <p>Unlike Chapter 17's quality gate -- which can reasonably tolerate a
 * pass rate below 100%, since one weak answer degrades a response without
 * exposing anything -- this gate requires exactly 1.0. A single case where
 * a document reaches someone never cleared to see it is not "mostly
 * fixed." Quality and security regressions are not the same kind of
 * failure, and this gate deliberately does not treat them with the same
 * tolerance.
 */
@Service
public class KnowledgeAssistantReleaseGate {

    public double securityComplianceRate(List<SecurityEvaluationCase> cases, KnowledgeAssistant assistant) {
        long compliantCount = cases.stream().filter(testCase -> isCompliant(testCase, assistant)).count();
        return (double) compliantCount / cases.size();
    }

    public boolean meetsReleaseGate(double complianceRate) {
        return complianceRate == 1.0;
    }

    private boolean isCompliant(SecurityEvaluationCase testCase, KnowledgeAssistant assistant) {
        KnowledgeAssistantAnswer answer = assistant.answer(
                testCase.question(), testCase.user(), testCase.candidatePool());

        if (answer.prompt() == null) {
            return true;
        }

        return testCase.candidatePool().stream()
                .filter(document -> !testCase.user().canAccess(
                        (AccessScope) document.getMetadata().get(AccessFilter.REQUIRED_SCOPE_METADATA_KEY)))
                .noneMatch(document -> answer.prompt().contains(document.getText()));
    }
}
