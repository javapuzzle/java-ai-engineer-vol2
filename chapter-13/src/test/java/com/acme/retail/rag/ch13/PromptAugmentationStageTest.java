package com.acme.retail.rag.ch13;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PromptAugmentationStageTest {

    private final PromptAugmentationStage stage = new PromptAugmentationStage();

    @Test
    void buildsAPromptContainingTheQuestionAndEveryChunkInChunksForPrompt() {
        PipelineContext context = new PipelineContext("What is the warranty period for the ProBlend500?");
        context.setChunksForPrompt(List.of("The ProBlend500 was discontinued in 2019."));

        PipelineContext result = stage.process(context);

        assertThat(result.getFinalPrompt()).contains("What is the warranty period for the ProBlend500?");
        assertThat(result.getFinalPrompt()).contains("The ProBlend500 was discontinued in 2019.");
        assertThat(result.getFinalPrompt()).contains("ONLY the context below");
    }
}
