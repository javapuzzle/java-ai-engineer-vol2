package com.acme.retail.rag.ch16;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds two versions of the same prompt to make the grounding instruction
 * itself a visible, checkable design decision rather than an assumed
 * default. The permissive version is what Sarah's very first attempt at
 * calling a model looked like, back before Chapter 8 existed; the strict
 * version is the instruction every chapter since has quietly relied on.
 */
@Service
public class GroundedPromptBuilder {

    public String buildPermissivePrompt(String question, List<String> contextChunks) {
        String contextBlock = String.join("\n", contextChunks);

        return """
                Context:
                %s

                Question: %s
                """.formatted(contextBlock, question);
    }

    public String buildStrictPrompt(String question, List<String> contextChunks) {
        String contextBlock = contextChunks.stream().collect(Collectors.joining("\n"));

        return """
                Answer the question using ONLY the context below. \
                Do not use any outside knowledge. \
                If the answer is not contained in the context, say you don't know.

                Context:
                %s

                Question: %s
                """.formatted(contextBlock, question);
    }
}
