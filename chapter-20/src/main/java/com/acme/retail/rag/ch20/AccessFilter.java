package com.acme.retail.rag.ch20;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Filters candidates by permission BEFORE anything downstream ever sees
 * them -- Chapter 18's whole lesson, unchanged in shape, reused here as
 * the capstone's non-negotiable first step. Every other stage in this
 * pipeline, including caching (see {@link CachingKnowledgeAssistantService}
 * and the rejected {@link QuestionOnlyAnswerCache}), is built around the
 * assumption that this filter has already run.
 */
@Service
public class AccessFilter {

    public static final String REQUIRED_SCOPE_METADATA_KEY = "requiredScope";

    public List<Document> filterByPermission(List<Document> candidates, UserPermissions user) {
        return candidates.stream()
                .filter(chunk -> user.canAccess((AccessScope) chunk.getMetadata().get(REQUIRED_SCOPE_METADATA_KEY)))
                .toList();
    }
}
