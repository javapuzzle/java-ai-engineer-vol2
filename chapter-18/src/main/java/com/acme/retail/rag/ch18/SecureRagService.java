package com.acme.retail.rag.ch18;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The full flow: filter candidates by permission first, THEN build the
 * prompt from whatever survives. A chunk the user isn't cleared for is
 * excluded before {@code AugmentedPromptBuilder}-style prompt construction
 * ever runs -- it structurally cannot appear in the prompt, regardless of
 * how the model might have been instructed to behave.
 */
@Service
public class SecureRagService {

    private final AccessFilter accessFilter;

    public SecureRagService(AccessFilter accessFilter) {
        this.accessFilter = accessFilter;
    }

    public String buildPromptFor(UserPermissions user, String question, List<Document> allCandidates) {
        List<Document> permittedChunks = accessFilter.filterByPermission(allCandidates, user);

        String contextBlock = permittedChunks.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        return """
                Answer the question using ONLY the context below. \
                If the answer is not contained in the context, say you don't know.

                Context:
                %s

                Question: %s
                """.formatted(contextBlock, question);
    }
}
