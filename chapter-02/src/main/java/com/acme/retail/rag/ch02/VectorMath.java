package com.acme.retail.rag.ch02;

/**
 * Spring AI's {@code EmbeddingModel} returns a plain {@code float[]} per
 * text -- there is no public {@code VectorStore} artifact cached in this
 * offline build environment (confirmed: no network path to fetch it), but
 * {@code EmbeddingModel} and {@code Document} themselves are real Spring AI
 * types (spring-ai-model, spring-ai-commons). Spring AI ships no public
 * cosine-similarity utility of its own -- every vector store computes
 * similarity internally, privately, per implementation -- so this one
 * method is a genuine, small gap this chapter has to fill by hand.
 */
public final class VectorMath {

    private VectorMath() {
    }

    public static double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Embeddings must have the same dimensionality to compare");
        }

        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            magnitudeA += a[i] * a[i];
            magnitudeB += b[i] * b[i];
        }

        double denominator = Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB);
        if (denominator == 0.0) {
            return 0.0;
        }

        return dotProduct / denominator;
    }
}
