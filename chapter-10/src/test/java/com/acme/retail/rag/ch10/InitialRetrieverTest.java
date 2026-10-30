package com.acme.retail.rag.ch10;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InitialRetrieverTest {

    private static final String QUERY = "Can I return the blender if the box is opened?";
    private static final String CHUNK_A_TEXT =
            "You may return an opened blender within 30 days for a full refund.";
    private static final String CHUNK_B_TEXT =
            "Return items must be shipped in the original box within 14 days of delivery.";

    private final Document chunkA = new Document("chunk-a", CHUNK_A_TEXT, Map.of());
    private final Document chunkB = new Document("chunk-b", CHUNK_B_TEXT, Map.of());

    private final StubEmbeddingModel embeddingModel = new StubEmbeddingModel(Map.of(
            QUERY, new float[]{0.70f, 0.20f, 0.10f},
            CHUNK_A_TEXT, new float[]{0.55f, 0.35f, 0.10f},
            CHUNK_B_TEXT, new float[]{0.72f, 0.18f, 0.08f}
    ));

    private final InitialRetriever retriever = new InitialRetriever(List.of(chunkA, chunkB), embeddingModel);

    @Test
    void embeddingSimilarityAloneRanksTheLessSpecificChunkFirst() {
        List<Document> results = retriever.retrieve(QUERY);

        assertThat(results.get(0).getId()).isEqualTo("chunk-b");
        assertThat(results.get(1).getId()).isEqualTo("chunk-a");
    }
}
