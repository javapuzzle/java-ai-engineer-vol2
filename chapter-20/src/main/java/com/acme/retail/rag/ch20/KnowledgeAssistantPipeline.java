package com.acme.retail.rag.ch20;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Composes every prior stage into one pipeline: route, then -- only for
 * {@code DOCUMENT_RAG} -- filter by permission, grade for relevance, and
 * build the prompt. The filter runs first every time, structurally, not
 * because a caller remembered to call it first: there is no code path
 * through this class where {@code correctiveGrading} or {@code
 * promptBuilder} ever see a candidate {@code accessFilter} has not already
 * cleared.
 */
@Service
public class KnowledgeAssistantPipeline implements KnowledgeAssistant {

    private final KnowledgeAssistantRouter router;
    private final AccessFilter accessFilter;
    private final CorrectiveGradingService correctiveGrading;
    private final KnowledgeAssistantPromptBuilder promptBuilder;

    public KnowledgeAssistantPipeline(KnowledgeAssistantRouter router,
                                       AccessFilter accessFilter,
                                       CorrectiveGradingService correctiveGrading,
                                       KnowledgeAssistantPromptBuilder promptBuilder) {
        this.router = router;
        this.accessFilter = accessFilter;
        this.correctiveGrading = correctiveGrading;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public KnowledgeAssistantAnswer answer(String question, UserPermissions user, List<Document> candidatePool) {
        QueryRoute route = router.route(question);
        if (route != QueryRoute.DOCUMENT_RAG) {
            return KnowledgeAssistantAnswer.routedElsewhere(route);
        }

        List<Document> permitted = accessFilter.filterByPermission(candidatePool, user);
        List<Document> relevant = correctiveGrading.gradeRelevant(question, permitted);

        if (relevant.isEmpty()) {
            return KnowledgeAssistantAnswer.unanswerable();
        }

        return KnowledgeAssistantAnswer.answerable(promptBuilder.build(question, relevant));
    }
}
