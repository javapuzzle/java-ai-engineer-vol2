package com.acme.retail.rag.ch11;

/**
 * Spring AI ships no public cosine-similarity utility -- every vector
 * store computes it privately, per implementation -- so this is a genuine,
 * small gap this chapter fills by hand, operating on the plain
 * {@code float[]} shape {@code EmbeddingModel.embed(String)} returns.
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
