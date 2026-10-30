package com.acme.retail.rag.ch08;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AugmentedPromptBuilderTest {

    private final AugmentedPromptBuilder promptBuilder = new AugmentedPromptBuilder();

    private final Document returnPolicyChunk = new Document(
            "return-policy-1",
            "Opened electronics may be returned within 30 days of purchase for a full refund.",
            Map.of("source", "return-policy.pdf"));

    @Test
    void includesTheQuestionTheContextAndAGroundingInstruction() {
        String prompt = promptBuilder.build("Can I return my blender?", List.of(returnPolicyChunk));

        assertThat(prompt).contains("Can I return my blender?");
        assertThat(prompt).contains("Opened electronics may be returned within 30 days");
        assertThat(prompt).contains("ONLY the context below");
        assertThat(prompt).contains("say you don't know");
    }

    @Test
    void citesTheSourceDocumentForEveryIncludedChunk() {
        String prompt = promptBuilder.build("Can I return my blender?", List.of(returnPolicyChunk));

        assertThat(prompt).contains("[Source: return-policy.pdf]");
    }

    @Test
    void omitsAnyChunkThatWasNotPassedIn() {
        String prompt = promptBuilder.build("Can I return my blender?", List.of(returnPolicyChunk));

        assertThat(prompt).doesNotContain("Extended warranty covers manufacturing defects");
    }
}
