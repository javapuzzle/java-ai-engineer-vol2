package com.acme.retail.rag.ch13;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RelevanceGradingStageTest {

    private static final String WRONG_MODEL_CHUNK =
            "The ProBlend900 comes with a two-year manufacturer's warranty covering defects in materials and workmanship.";
    private static final String CORRECT_MODEL_CHUNK =
            "The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty support.";

    private final RelevanceGradingStage stage = new RelevanceGradingStage();

    @Test
    void narrowsChunksForPromptDownToOnlyTheNamedModel() {
        PipelineContext context = new PipelineContext("What is the warranty period for the ProBlend500?");
        context.setChunksForPrompt(List.of(WRONG_MODEL_CHUNK, CORRECT_MODEL_CHUNK));

        PipelineContext result = stage.process(context);

        assertThat(result.getChunksForPrompt()).containsExactly(CORRECT_MODEL_CHUNK);
    }

    @Test
    void leavesChunksUntouchedWhenTheQuestionNamesNoSpecificModel() {
        PipelineContext context = new PipelineContext("What is your general warranty policy?");
        context.setChunksForPrompt(List.of(WRONG_MODEL_CHUNK, CORRECT_MODEL_CHUNK));

        PipelineContext result = stage.process(context);

        assertThat(result.getChunksForPrompt()).containsExactly(WRONG_MODEL_CHUNK, CORRECT_MODEL_CHUNK);
    }
}
