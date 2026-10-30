package com.acme.retail.rag.ch10;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordOverlapRerankerTest {

    private static final String QUERY = "Can I return the blender if the box is opened?";

    private final Document chunkA = new Document(
            "chunk-a", "You may return an opened blender within 30 days for a full refund.", Map.of())
            .mutate().score(0.9594).build();
    private final Document chunkB = new Document(
            "chunk-b", "Return items must be shipped in the original box within 14 days of delivery.", Map.of())
            .mutate().score(0.9987).build();

    private final KeywordOverlapReranker reranker = new KeywordOverlapReranker();

    @Test
    void movesTheChunkWithMoreMeaningfulWordOverlapAboveTheChunkWithHigherEmbeddingScore() {
        List<Document> initialOrder = List.of(chunkB, chunkA);

        List<Document> reranked = reranker.rerank(QUERY, initialOrder);

        assertThat(reranked.get(0).getId()).isEqualTo("chunk-a");
        assertThat(reranked.get(1).getId()).isEqualTo("chunk-b");
    }
}
