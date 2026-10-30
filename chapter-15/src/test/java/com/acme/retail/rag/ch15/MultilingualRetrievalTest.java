package com.acme.retail.rag.ch15;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MultilingualRetrievalTest {

    private static final String SPANISH_QUESTION = "¿Puedo devolver la licuadora si la abrí?";
    private static final String ENGLISH_RETURN_POLICY =
            "Opened electronics may be returned within 30 days of purchase for a full refund.";
    private static final String ENGLISH_WARRANTY_POLICY =
            "Extended warranty covers manufacturing defects for two years from purchase date.";

    private final KeywordMatcher keywordMatcher = new KeywordMatcher();

    @Test
    void literalKeywordMatchingFailsCompletelyAcrossLanguages() {
        boolean matched = keywordMatcher.matches(SPANISH_QUESTION, ENGLISH_RETURN_POLICY);

        assertThat(matched)
                .as("a Spanish question and an English document share zero literal words, by construction")
                .isFalse();
    }

    /**
     * These coordinates are hand-placed, standing in for what a real
     * multilingual embedding model would produce: the Spanish question and
     * its English semantic equivalent land close together, despite sharing
     * no alphabet-level overlap at all. The actual model choice -- one
     * genuinely trained on cross-lingual data -- is Chapter 3's decision,
     * extended to cover the languages Acme's customers actually use. Since
     * Mockito's inline mock maker can't self-attach in this build
     * environment, the embedding model is a small hand-written class
     * implementing the real {@code EmbeddingModel} interface directly.
     */
    @Test
    void embeddingRetrievalFindsTheCorrectEnglishDocumentForASpanishQuestion() {
        EmbeddingModel embeddingModel = new EmbeddingModel() {
            @Override
            public float[] embed(String text) {
                return switch (text) {
                    case SPANISH_QUESTION -> new float[]{0.88f, 0.14f, 0.02f};
                    case ENGLISH_RETURN_POLICY -> new float[]{0.90f, 0.10f, 0.05f};
                    case ENGLISH_WARRANTY_POLICY -> new float[]{0.05f, 0.15f, 0.95f};
                    default -> throw new IllegalStateException("No hand-placed embedding stubbed for: " + text);
                };
            }

            @Override
            public float[] embed(Document document) {
                return embed(document.getText());
            }

            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                throw new UnsupportedOperationException("Not used by this chapter's tests");
            }
        };

        Document returnPolicyDoc = new Document("return-policy", ENGLISH_RETURN_POLICY, Map.of());
        Document warrantyDoc = new Document("warranty-policy", ENGLISH_WARRANTY_POLICY, Map.of());

        CrossLingualRetriever retriever = new CrossLingualRetriever(List.of(returnPolicyDoc, warrantyDoc), embeddingModel);

        List<Document> results = retriever.retrieveTopK(SPANISH_QUESTION, 1);

        assertThat(results).extracting(Document::getId).containsExactly("return-policy");
    }
}
