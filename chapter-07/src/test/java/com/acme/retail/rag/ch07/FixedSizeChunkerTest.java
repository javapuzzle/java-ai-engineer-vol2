package com.acme.retail.rag.ch07;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FixedSizeChunkerTest {

    private final FixedSizeChunker chunker = new FixedSizeChunker(40);

    private static final String KEY_SENTENCE =
            "Opened electronics may be returned within 30 days of purchase for a full refund.";

    private static final String POLICY_TEXT =
            "Returns are accepted for most items. " + KEY_SENTENCE + " Warranty claims are handled separately.";

    private final Document sourceDocument = new Document("return-policy", POLICY_TEXT, Map.of());

    @Test
    void aChunkSizeSmallerThanTheKeySentenceGuaranteesItIsSplitAcrossChunks() {
        assertThat(40).isLessThan(KEY_SENTENCE.length());

        List<Document> chunks = chunker.apply(List.of(sourceDocument));

        assertThat(chunks).extracting(Document::getText).noneMatch(text -> text.contains(KEY_SENTENCE));
    }

    @Test
    void reassemblingAllChunksReproducesTheOriginalTextExactly() {
        List<Document> chunks = chunker.apply(List.of(sourceDocument));

        String reassembled = chunks.stream().map(Document::getText).reduce("", String::concat);

        assertThat(reassembled).isEqualTo(POLICY_TEXT);
    }
}
