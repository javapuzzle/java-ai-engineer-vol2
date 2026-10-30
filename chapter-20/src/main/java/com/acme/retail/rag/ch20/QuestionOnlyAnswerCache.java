package com.acme.retail.rag.ch20;

import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The caching shape proposed -- and rejected, live, during Sarah's
 * capstone design review -- for the exact same latency reason every real
 * caching layer gets proposed for: identical questions were being
 * recomputed from scratch. This version memoizes by question text alone,
 * with no awareness that "permissions" exist at all.
 *
 * <p>Deliberately NOT a {@code @Service}: this class is never wired into
 * the running assistant. It exists solely as a permanent, executable
 * regression fixture (see {@code QuestionOnlyAnswerCacheTest}), proving
 * exactly what goes wrong if this shape is ever reintroduced -- a senior
 * staff member's answer, computed once, gets handed unmodified to the
 * next person who happens to ask the same question, regardless of what
 * that second person is actually cleared to see.
 */
public class QuestionOnlyAnswerCache implements KnowledgeAssistant {

    private final KnowledgeAssistantPipeline pipeline;
    private final Map<String, KnowledgeAssistantAnswer> cache = new ConcurrentHashMap<>();

    public QuestionOnlyAnswerCache(KnowledgeAssistantPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public KnowledgeAssistantAnswer answer(String question, UserPermissions user, List<Document> candidatePool) {
        return cache.computeIfAbsent(question, q -> pipeline.answer(q, user, candidatePool));
    }
}
