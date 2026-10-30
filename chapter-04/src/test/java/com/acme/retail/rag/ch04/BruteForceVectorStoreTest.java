package com.acme.retail.rag.ch04;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class BruteForceVectorStoreTest {

    private static final String RETURN_TEXT =
            "Opened electronics may be returned within 30 days of purchase for a full refund.";
    private static final String WARRANTY_TEXT =
            "Extended warranty covers manufacturing defects for two years from purchase date.";
    private static final String SHIPPING_TEXT =
            "Standard shipping takes 3 to 5 business days within the continental US.";
    private static final String SEND_BACK_QUESTION = "Can I send back the blender I bought last week?";

    private final Document returnPolicy = new Document("return-policy", RETURN_TEXT, Map.of());
    private final Document warrantyPolicy = new Document("warranty-policy", WARRANTY_TEXT, Map.of());
    private final Document shippingPolicy = new Document("shipping-policy", SHIPPING_TEXT, Map.of());

    private StubEmbeddingModel handPlacedEmbeddingModel() {
        return new StubEmbeddingModel(Map.of(
                RETURN_TEXT, new float[]{0.90f, 0.10f, 0.05f},
                WARRANTY_TEXT, new float[]{0.05f, 0.15f, 0.95f},
                SHIPPING_TEXT, new float[]{0.40f, 0.85f, 0.30f},
                SEND_BACK_QUESTION, new float[]{0.88f, 0.14f, 0.02f}
        ));
    }

    @Test
    void returnsTheClosestDocumentFirst() {
        BruteForceVectorStore store = new BruteForceVectorStore(
                List.of(returnPolicy, warrantyPolicy, shippingPolicy), handPlacedEmbeddingModel());

        List<ScoredResult> results = store.search(SEND_BACK_QUESTION, 1);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).documentId()).isEqualTo("return-policy");
    }

    @Test
    void ranksAllThreeDocumentsInDescendingSimilarityOrder() {
        BruteForceVectorStore store = new BruteForceVectorStore(
                List.of(returnPolicy, warrantyPolicy, shippingPolicy), handPlacedEmbeddingModel());

        List<ScoredResult> results = store.search(SEND_BACK_QUESTION, 3);

        assertThat(results).extracting(ScoredResult::documentId)
                .containsExactly("return-policy", "shipping-policy", "warranty-policy");
        assertThat(results.get(0).similarity()).isGreaterThan(results.get(1).similarity());
        assertThat(results.get(1).similarity()).isGreaterThan(results.get(2).similarity());
    }

    @Test
    void requestingMoreResultsThanTheCorpusSizeReturnsEverythingWithoutError() {
        BruteForceVectorStore store = new BruteForceVectorStore(
                List.of(returnPolicy, warrantyPolicy, shippingPolicy), handPlacedEmbeddingModel());

        List<ScoredResult> results = store.search(SEND_BACK_QUESTION, 100);

        assertThat(results).hasSize(3);
    }

    @Test
    void searchingIsExactRegardlessOfCorpusSize() {
        Map<String, float[]> embeddingsByText = new HashMap<>();
        embeddingsByText.put(RETURN_TEXT, new float[]{0.90f, 0.10f, 0.05f});
        embeddingsByText.put(SEND_BACK_QUESTION, new float[]{0.88f, 0.14f, 0.02f});

        List<Document> fillerDocuments = IntStream.range(0, 5_000)
                .mapToObj(i -> {
                    String fillerText = "filler-text-" + i;
                    embeddingsByText.put(fillerText, new float[]{0.01f, 0.01f, 0.01f});
                    return new Document("filler-" + i, fillerText, Map.of());
                })
                .toList();

        List<Document> corpusWithOneRealMatch = new ArrayList<>(fillerDocuments);
        corpusWithOneRealMatch.add(returnPolicy);

        BruteForceVectorStore largeStore = new BruteForceVectorStore(
                corpusWithOneRealMatch, new StubEmbeddingModel(embeddingsByText));

        List<ScoredResult> results = largeStore.search(SEND_BACK_QUESTION, 1);

        assertThat(largeStore.corpusSize()).isEqualTo(5_001);
        assertThat(results.get(0).documentId()).isEqualTo("return-policy");
    }
}
