package com.acme.retail.rag.ch11;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HybridSearchTest {

    private static final String QUERY = "XR-4400 return policy";

    private final Document productDoc = new Document(
            "product-xr4400", "The XR-4400 blender is currently in stock.", Map.of());
    private final Document returnPolicyDoc = new Document(
            "return-policy", "Our return policy allows returns within 30 days of purchase.", Map.of());
    private final Document shippingDoc = new Document(
            "shipping-info", "Standard shipping takes 3 to 5 business days within the continental US.", Map.of());

    private final List<Document> allDocuments = List.of(productDoc, returnPolicyDoc, shippingDoc);

    private final Map<String, Double> termRarityWeights = Map.of(
            "xr", 5.0, "4400", 5.0, "return", 1.0, "policy", 1.0);

    private final StubEmbeddingModel embeddingModel = new StubEmbeddingModel(Map.of(
            QUERY, new float[]{0.20f, 0.75f, 0.05f},
            "The XR-4400 blender is currently in stock.", new float[]{0.10f, 0.15f, 0.85f},
            "Our return policy allows returns within 30 days of purchase.", new float[]{0.18f, 0.78f, 0.04f},
            "Standard shipping takes 3 to 5 business days within the continental US.", new float[]{0.85f, 0.10f, 0.30f}
    ));

    private final LexicalSearch lexicalSearch = new LexicalSearch(termRarityWeights);
    private final DenseSearch denseSearch = new DenseSearch(embeddingModel);
    private final HybridSearch hybridSearch = new HybridSearch(lexicalSearch, denseSearch, 1.0);

    @Test
    void lexicalSearchRanksTheExactSkuMatchFirstDueToItsRareDistinctiveTerms() {
        List<RankedDocument> lexicalRanks = lexicalSearch.rank(QUERY, allDocuments);

        assertThat(lexicalRanks.get(0).documentId()).isEqualTo("product-xr4400");
    }

    @Test
    void denseSearchAloneRanksTheExactSkuMatchLastBehindEvenAnIrrelevantDocument() {
        List<RankedDocument> denseRanks = denseSearch.rank(QUERY, allDocuments);

        assertThat(denseRanks.get(denseRanks.size() - 1).documentId()).isEqualTo("product-xr4400");
    }

    @Test
    void denseOnlyTopTwoCompletelyMissesTheExactSkuMatch() {
        List<RankedDocument> denseRanks = denseSearch.rank(QUERY, allDocuments);
        List<String> denseTopTwo = denseRanks.stream().limit(2).map(RankedDocument::documentId).toList();

        assertThat(denseTopTwo).doesNotContain("product-xr4400");
    }

    @Test
    void hybridSearchTopTwoRecoversTheExactSkuMatchThatDenseAloneMissed() {
        List<String> hybridRanking = hybridSearch.search(QUERY, allDocuments);
        List<String> hybridTopTwo = hybridRanking.stream().limit(2).toList();

        assertThat(hybridTopTwo).contains("product-xr4400");
        assertThat(hybridTopTwo).doesNotContain("shipping-info");
    }
}
