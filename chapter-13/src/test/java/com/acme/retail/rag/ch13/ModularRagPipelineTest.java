package com.acme.retail.rag.ch13;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The central claim of this chapter: the exact same stage classes, with no
 * code changes to any of them, produce a meaningfully different (and
 * meaningfully more correct) result purely by changing which stages appear
 * in the list.
 */
class ModularRagPipelineTest {

    private static final String QUESTION = "What is the warranty period for the ProBlend500?";

    private static final Document WRONG_MODEL_DOC = new Document(
            "problend900-warranty",
            "The ProBlend900 comes with a two-year manufacturer's warranty covering defects in materials and workmanship.",
            Map.of());
    private static final Document CORRECT_MODEL_DOC = new Document(
            "problend500-warranty",
            "The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty support.",
            Map.of());

    private final List<Document> corpus = List.of(WRONG_MODEL_DOC, CORRECT_MODEL_DOC);

    @Test
    void withoutTheGradingStageTheWrongProductChunkLeaksIntoTheFinalPrompt() {
        ModularRagPipeline pipelineMissingGrading = new ModularRagPipeline(List.of(
                new RetrievalStage(corpus),
                new PromptAugmentationStage()
        ));

        PipelineContext result = pipelineMissingGrading.run(QUESTION);

        assertThat(result.getFinalPrompt()).contains(WRONG_MODEL_DOC.getText());
        assertThat(result.getFinalPrompt()).contains(CORRECT_MODEL_DOC.getText());
    }

    @Test
    void addingTheGradingStageToTheSameListExcludesTheWrongProductChunk() {
        ModularRagPipeline pipelineWithGrading = new ModularRagPipeline(List.of(
                new RetrievalStage(corpus),
                new RelevanceGradingStage(),
                new PromptAugmentationStage()
        ));

        PipelineContext result = pipelineWithGrading.run(QUESTION);

        assertThat(result.getFinalPrompt()).doesNotContain(WRONG_MODEL_DOC.getText());
        assertThat(result.getFinalPrompt()).contains(CORRECT_MODEL_DOC.getText());
    }
}
