package com.acme.retail.rag.ch16;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GroundedPromptBuilderTest {

    private final GroundedPromptBuilder promptBuilder = new GroundedPromptBuilder();
    private static final List<String> CONTEXT = List.of(
            "The XR-4400 blender has a 2-year manufacturer's warranty covering defects in materials and workmanship.");

    @Test
    void thePermissivePromptContainsNoGroundingInstructionAtAll() {
        String prompt = promptBuilder.buildPermissivePrompt("What's the warranty on the XR-4400?", CONTEXT);

        assertThat(prompt).doesNotContain("ONLY");
        assertThat(prompt).doesNotContain("don't know");
    }

    @Test
    void theStrictPromptExplicitlyInstructsTheModelToStayWithinTheContext() {
        String prompt = promptBuilder.buildStrictPrompt("What's the warranty on the XR-4400?", CONTEXT);

        assertThat(prompt).contains("ONLY the context below");
        assertThat(prompt).contains("Do not use any outside knowledge");
        assertThat(prompt).contains("say you don't know");
    }

    @Test
    void bothPromptsStillIncludeTheSameQuestionAndContext() {
        String question = "What's the warranty on the XR-4400?";

        String permissive = promptBuilder.buildPermissivePrompt(question, CONTEXT);
        String strict = promptBuilder.buildStrictPrompt(question, CONTEXT);

        for (String prompt : List.of(permissive, strict)) {
            assertThat(prompt).contains(question);
            assertThat(prompt).contains(CONTEXT.get(0));
        }
    }
}
