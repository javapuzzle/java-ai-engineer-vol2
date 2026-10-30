package com.acme.retail.rag.ch08;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChunkVectorStoreTest {

    private static final String RETURN_TEXT =
            "Opened electronics may be returned within 30 days of purchase for a full refund.";
    private static final String WARRANTY_TEXT =
            "Extended warranty covers manufacturing defects for two years from purchase date.";
    private static final String SHIPPING_TEXT =
            "Standard shipping takes 3 to 5 business days within the continental US.";
    private static final String RETURN_QUESTION = "Can I return my blender?";

    private final Document returnPolicyChunk = new Document(
            "return-policy-1", RETURN_TEXT, Map.of("source", "return-policy.pdf"));
    private final Document warrantyChunk = new Document(
            "warranty-1", WARRANTY_TEXT, Map.of("source", "warranty.pdf"));
    private final Document shippingChunk = new Document(
            "shipping-1", SHIPPING_TEXT, Map.of("source", "shipping.pdf"));

    private final StubEmbeddingModel embeddingModel = new StubEmbeddingModel(Map.of(
            RETURN_TEXT, new float[]{0.90f, 0.10f, 0.05f},
            WARRANTY_TEXT, new float[]{0.05f, 0.15f, 0.95f},
            SHIPPING_TEXT, new float[]{0.40f, 0.85f, 0.30f},
            RETURN_QUESTION, new float[]{0.88f, 0.14f, 0.02f}
    ));

    private final ChunkVectorStore store =
            new ChunkVectorStore(List.of(returnPolicyChunk, warrantyChunk, shippingChunk), embeddingModel);

    @Test
    void retrievesTheMostSimilarChunkFirst() {
        List<Document> results = store.retrieveTopK(RETURN_QUESTION, 1);

        assertThat(results).extracting(Document::getId).containsExactly("return-policy-1");
    }

    @Test
    void retrievingFewerThanTheFullCorpusExcludesTheLeastSimilarChunks() {
        List<Document> results = store.retrieveTopK(RETURN_QUESTION, 2);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Document::getId).doesNotContain("warranty-1");
    }
}
