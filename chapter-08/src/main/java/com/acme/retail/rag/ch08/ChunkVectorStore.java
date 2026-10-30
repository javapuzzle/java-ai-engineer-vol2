package com.acme.retail.rag.ch08;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exact top-k retrieval over the indexed knowledge base -- Chapter 4's
 * brute-force approach, now doing real work in the pipeline. A production
 * system swaps this for Chapter 5's chosen vector database without
 * changing anything else here; only this class's internals would change,
 * because {@code spring-ai-vector-store} (which ships the real
 * {@code VectorStore} interface) isn't resolvable in this offline build
 * environment. Documents and embeddings are the real Spring AI types
 * ({@code Document}, {@code EmbeddingModel}) either way.
 */
@Service
public class ChunkVectorStore {

    private final List<Document> documents;
    private final Map<String, float[]> embeddingsByDocumentId;
    private final EmbeddingModel embeddingModel;

    public ChunkVectorStore(List<Document> documents, EmbeddingModel embeddingModel) {
        this.documents = documents;
        this.embeddingModel = embeddingModel;
        this.embeddingsByDocumentId = new HashMap<>();

        for (Document document : documents) {
            embeddingsByDocumentId.put(document.getId(), embeddingModel.embed(document.getText()));
        }
    }

    public List<Document> retrieveTopK(String query, int topK) {
        float[] queryEmbedding = embeddingModel.embed(query);

        return documents.stream()
                .sorted(Comparator.comparingDouble(
                        (Document document) -> VectorMath.cosineSimilarity(
                                queryEmbedding, embeddingsByDocumentId.get(document.getId()))).reversed())
                .limit(topK)
                .toList();
    }
}
