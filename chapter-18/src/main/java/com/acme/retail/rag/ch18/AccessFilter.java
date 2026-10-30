package com.acme.retail.rag.ch18;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Filters candidates by permission BEFORE anything downstream ever sees
 * them. This is the whole lesson of this chapter: the check happens at
 * retrieval time, on the candidate list, not afterward on the model's
 * output -- a restricted chunk that never enters this method's return
 * value cannot possibly leak through generation, because generation never
 * receives it. Operates on real Spring AI {@link Document} instances,
 * with the required access scope carried in the document's own metadata
 * map -- exactly where a real vector store's metadata filtering would read
 * it from.
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
