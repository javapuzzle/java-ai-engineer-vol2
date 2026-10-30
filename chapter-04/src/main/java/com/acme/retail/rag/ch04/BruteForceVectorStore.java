package com.acme.retail.rag.ch04;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exact nearest-neighbor search: compare the query against every single
 * stored document, every time. Correct by construction -- there is no
 * approximation here, so it's the right tool for proving what "correct"
 * looks like before Chapter 5 picks a real vector database that trades a
 * little of this exactness for the speed a brute-force scan can't offer
 * once the corpus stops being small.
 *
 * <p>Documents are real Spring AI {@link Document} instances. Embeddings
 * are computed once, at construction time, via the injected
 * {@link EmbeddingModel} -- the same real interface a production
 * {@code VectorStore} implementation calls internally; this class is only
 * standing in for the store itself, which isn't resolvable in this offline
 * build environment (no cached {@code spring-ai-vector-store} artifact).
 */
@Service
public class BruteForceVectorStore {

    private final List<Document> documents;
    private final Map<String, float[]> embeddingsByDocumentId;
    private final EmbeddingModel embeddingModel;

    public BruteForceVectorStore(List<Document> documents, EmbeddingModel embeddingModel) {
        this.documents = documents;
        this.embeddingModel = embeddingModel;
        this.embeddingsByDocumentId = new HashMap<>();

        for (Document document : documents) {
            embeddingsByDocumentId.put(document.getId(), embeddingModel.embed(document.getText()));
        }
    }

    public List<ScoredResult> search(String query, int topK) {
        float[] queryEmbedding = embeddingModel.embed(query);

        return documents.stream()
                .map(document -> new ScoredResult(document.getId(),
                        VectorMath.cosineSimilarity(queryEmbedding, embeddingsByDocumentId.get(document.getId()))))
                .sorted(Comparator.comparingDouble(ScoredResult::similarity).reversed())
                .limit(topK)
                .toList();
    }

    public int corpusSize() {
        return documents.size();
    }
}
