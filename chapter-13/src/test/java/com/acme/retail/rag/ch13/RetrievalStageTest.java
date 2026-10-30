package com.acme.retail.rag.ch13;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RetrievalStageTest {

    private static final Document WRONG_MODEL_DOC = new Document(
            "problend900-warranty",
            "The ProBlend900 comes with a two-year manufacturer's warranty covering defects in materials and workmanship.",
            Map.of());
    private static final Document CORRECT_MODEL_DOC = new Document(
            "problend500-warranty",
            "The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty support.",
            Map.of());
    private static final Document UNRELATED_DOC = new Document(
            "shipping-info",
            "Standard shipping takes 3 to 5 business days within the continental US.",
            Map.of());

    private final RetrievalStage stage =
            new RetrievalStage(List.of(WRONG_MODEL_DOC, CORRECT_MODEL_DOC, UNRELATED_DOC));

    @Test
    void retrievesBothWarrantyChunksBecauseBothShareTheWordWarranty() {
        PipelineContext context = new PipelineContext("What is the warranty period for the ProBlend500?");

        PipelineContext result = stage.process(context);

        assertThat(result.getChunksForPrompt()).contains(WRONG_MODEL_DOC.getText(), CORRECT_MODEL_DOC.getText());
        assertThat(result.getChunksForPrompt()).doesNotContain(UNRELATED_DOC.getText());
    }
}
