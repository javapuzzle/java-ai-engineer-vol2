package com.acme.retail.rag.ch20;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The fix: cache by (question, permission-scope signature), never by
 * question alone. Two different users who happen to share the exact same
 * granted scopes still safely share a cache entry -- caching is not
 * disabled, only correctly scoped to the dimension that actually
 * determines what's safe to reuse.
 */
@Service
public class CachingKnowledgeAssistantService implements KnowledgeAssistant {

    private final KnowledgeAssistantPipeline pipeline;
    private final Map<CacheKey, KnowledgeAssistantAnswer> cache = new ConcurrentHashMap<>();

    public CachingKnowledgeAssistantService(KnowledgeAssistantPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public KnowledgeAssistantAnswer answer(String question, UserPermissions user, List<Document> candidatePool) {
        CacheKey key = new CacheKey(question, user.grantedScopes());
        return cache.computeIfAbsent(key, k -> pipeline.answer(question, user, candidatePool));
    }

    public int cacheSize() {
        return cache.size();
    }

    private record CacheKey(String question, Set<AccessScope> scopeSignature) {
    }
}
