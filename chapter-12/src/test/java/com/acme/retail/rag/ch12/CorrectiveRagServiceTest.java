package com.acme.retail.rag.ch12;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectiveRagServiceTest {

    private final CorrectiveRagService service = new CorrectiveRagService(new ProductIdentifierRelevanceGrader());

    private static final String QUERY = "What is the warranty period for the ProBlend500?";
    private static final String WRONG_MODEL_CHUNK =
            "The ProBlend900 comes with a two-year manufacturer's warranty covering defects "
                    + "in materials and workmanship.";
    private static final String CORRECT_MODEL_CHUNK =
            "The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty support.";

    @Test
    void refusesToAnswerConfidentlyWhenEveryRetrievedChunkIsIrrelevant() {
        CorrectiveRagResult result = service.respond(QUERY, List.of(WRONG_MODEL_CHUNK));

        assertThat(result.answerable()).isFalse();
        assertThat(result.relevantChunkTexts()).isEmpty();
        assertThat(result.message()).contains("don't have enough relevant information");
    }

    @Test
    void answersUsingOnlyTheChunksThatSurviveGrading() {
        CorrectiveRagResult result = service.respond(QUERY, List.of(WRONG_MODEL_CHUNK, CORRECT_MODEL_CHUNK));

        assertThat(result.answerable()).isTrue();
        assertThat(result.relevantChunkTexts()).containsExactly(CORRECT_MODEL_CHUNK);
        assertThat(result.relevantChunkTexts()).doesNotContain(WRONG_MODEL_CHUNK);
    }
}
