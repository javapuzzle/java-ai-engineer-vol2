package com.acme.retail.rag.ch10;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * First-stage retrieval: rank documents by embedding similarity alone --
 * fast, and exactly what Chapter 8's Naive RAG already does. Embedding
 * similarity (a "bi-encoder" score, since the query and each document are
 * embedded independently, without ever looking at each other) is a
 * genuinely useful approximation of relevance, not a perfect one -- this
 * chapter exists because the gap between the two is real and measurable.
 * The similarity score is attached to a real Spring AI {@code Document}
 * via its own {@code score} field, rather than a custom wrapper type.
 */
@Service
public class InitialRetriever {

    private final List<Document> documents;
    private final EmbeddingModel embeddingModel;

    public InitialRetriever(List<Document> documents, EmbeddingModel embeddingModel) {
        this.documents = documents;
        this.embeddingModel = embeddingModel;
    }

    public List<Document> retrieve(String query) {
        float[] queryEmbedding = embeddingModel.embed(query);

        return documents.stream()
                .map(document -> document.mutate()
                        .score(VectorMath.cosineSimilarity(queryEmbedding, embeddingModel.embed(document.getText())))
                        .build())
                .sorted(Comparator.comparingDouble(Document::getScore).reversed())
                .toList();
    }
}
