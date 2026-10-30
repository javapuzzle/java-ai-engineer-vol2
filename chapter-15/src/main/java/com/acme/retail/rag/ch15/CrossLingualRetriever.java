package com.acme.retail.rag.ch15;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;

import java.util.Comparator;
import java.util.List;

/**
 * The same top-k retrieval every chapter since Chapter 8 has used. Nothing
 * about this class is language-aware -- it only compares vectors. Cross-
 * lingual retrieval isn't a special retrieval algorithm; it's a property
 * of the embedding model being used, which is exactly why Chapter 3's
 * "choose a model that fits the domain" advice extends directly to
 * "choose a model that covers the languages your customers actually use."
 * Operates on real Spring AI {@link Document} and {@link EmbeddingModel}
 * types.
 */
public class CrossLingualRetriever {

    private final List<Document> documents;
    private final EmbeddingModel embeddingModel;

    public CrossLingualRetriever(List<Document> documents, EmbeddingModel embeddingModel) {
        this.documents = documents;
        this.embeddingModel = embeddingModel;
    }

    public List<Document> retrieveTopK(String query, int topK) {
        float[] queryEmbedding = embeddingModel.embed(query);

        return documents.stream()
                .sorted(Comparator.comparingDouble(
                        (Document doc) -> VectorMath.cosineSimilarity(queryEmbedding, embeddingModel.embed(doc.getText())))
                        .reversed())
                .limit(topK)
                .toList();
    }
}
