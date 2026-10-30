package com.acme.retail.rag.ch02;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class VectorMathTest {

    @Test
    void identicalVectorsHaveSimilarityOfOne() {
        float[] vector = {1.0f, 2.0f, 3.0f};

        assertThat(VectorMath.cosineSimilarity(vector, vector)).isCloseTo(1.0, within(1e-6));
    }

    @Test
    void orthogonalVectorsHaveSimilarityOfZero() {
        float[] alongX = {1.0f, 0.0f};
        float[] alongY = {0.0f, 1.0f};

        assertThat(VectorMath.cosineSimilarity(alongX, alongY)).isCloseTo(0.0, within(1e-6));
    }

    @Test
    void oppositeVectorsHaveSimilarityOfNegativeOne() {
        float[] vector = {1.0f, 2.0f, 3.0f};
        float[] opposite = {-1.0f, -2.0f, -3.0f};

        assertThat(VectorMath.cosineSimilarity(vector, opposite)).isCloseTo(-1.0, within(1e-6));
    }

    @Test
    void magnitudeDoesNotAffectSimilarityOnlyDirectionDoes() {
        float[] vector = {1.0f, 2.0f};
        float[] scaledUp = {10.0f, 20.0f};

        assertThat(VectorMath.cosineSimilarity(vector, scaledUp)).isCloseTo(1.0, within(1e-6));
    }

    /**
     * These coordinates are hand-placed for teaching purposes, chosen to sit
     * close together in this tiny illustrative space -- not the real output
     * of calling {@code EmbeddingModel.embed(String)}. The point: two
     * paraphrases of "return" should end up with high cosine similarity
     * despite sharing zero literal words, which is exactly what
     * {@code KeywordMatcherTest} proved keyword matching cannot do.
     */
    @Test
    void illustrativeParaphrasesOfReturnAreCloseInVectorSpace() {
        float[] returnPolicyPhrasing = {0.90f, 0.10f, 0.05f};
        float[] sendBackPhrasing = {0.88f, 0.14f, 0.02f};

        assertThat(VectorMath.cosineSimilarity(returnPolicyPhrasing, sendBackPhrasing)).isGreaterThan(0.95);
    }

    @Test
    void illustrativeUnrelatedTopicsAreFarInVectorSpace() {
        float[] returnPolicyPhrasing = {0.90f, 0.10f, 0.05f};
        float[] warrantyCoveragePhrasing = {0.05f, 0.15f, 0.95f};

        assertThat(VectorMath.cosineSimilarity(returnPolicyPhrasing, warrantyCoveragePhrasing)).isLessThan(0.3);
    }

    @Test
    void mismatchedDimensionsAreRejected() {
        float[] twoDimensional = {1.0f, 2.0f};
        float[] threeDimensional = {1.0f, 2.0f, 3.0f};

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> VectorMath.cosineSimilarity(twoDimensional, threeDimensional));
    }
}
