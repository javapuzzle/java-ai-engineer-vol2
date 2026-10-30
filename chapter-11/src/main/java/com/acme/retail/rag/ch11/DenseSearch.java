package com.acme.retail.rag.ch11;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/** Dense (embedding-based) search: the same cosine-similarity ranking Chapters 4, 8, and 10 already established. */
@Service
public class DenseSearch {

    private final EmbeddingModel embeddingModel;

    public DenseSearch(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<RankedDocument> rank(String query, List<Document> documents) {
        float[] queryEmbedding = embeddingModel.embed(query);

        List<Document> sorted = documents.stream()
                .sorted(Comparator.comparingDouble(
                        (Document doc) -> VectorMath.cosineSimilarity(queryEmbedding, embeddingModel.embed(doc.getText())))
                        .reversed())
                .toList();

        return IntStream.range(0, sorted.size())
                .mapToObj(i -> new RankedDocument(sorted.get(i).getId(), i + 1))
                .toList();
    }
}
