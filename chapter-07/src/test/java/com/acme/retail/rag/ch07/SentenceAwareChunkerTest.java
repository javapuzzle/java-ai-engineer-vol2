package com.acme.retail.rag.ch07;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SentenceAwareChunkerTest {

    private final SentenceAwareChunker chunker = new SentenceAwareChunker(40);

    private static final String KEY_SENTENCE =
            "Opened electronics may be returned within 30 days of purchase for a full refund.";
    private static final String FIRST_SENTENCE = "Returns are accepted for most items.";
    private static final String THIRD_SENTENCE = "Warranty claims are handled separately.";

    private static final String POLICY_TEXT =
            FIRST_SENTENCE + " " + KEY_SENTENCE + " " + THIRD_SENTENCE;

    private final Document sourceDocument = new Document("return-policy", POLICY_TEXT, Map.of());

    @Test
    void keepsTheKeySentenceWholeInsideExactlyOneChunkEvenWithATightTargetSize() {
        assertThat(40).isLessThan(KEY_SENTENCE.length());

        List<Document> chunks = chunker.apply(List.of(sourceDocument));

        assertThat(chunks).extracting(Document::getText).anyMatch(text -> text.equals(KEY_SENTENCE));
    }

    @Test
    void everySentenceSurvivesWholeInExactlyOneChunk() {
        List<Document> chunks = chunker.apply(List.of(sourceDocument));
        List<String> chunkTexts = chunks.stream().map(Document::getText).toList();

        for (String sentence : List.of(FIRST_SENTENCE, KEY_SENTENCE, THIRD_SENTENCE)) {
            long chunksContainingThisSentenceIntact = chunkTexts.stream()
                    .filter(chunkText -> chunkText.contains(sentence))
                    .count();

            assertThat(chunksContainingThisSentenceIntact).isEqualTo(1);
        }
    }

    @Test
    void groupsShortSentencesTogetherInsteadOfOneChunkPerSentence() {
        String shortSentences = "Yes. No. Maybe. It depends on the item.";
        Document shortDocument = new Document("faq", shortSentences, Map.of());

        List<Document> chunks = new SentenceAwareChunker(100).apply(List.of(shortDocument));

        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0).getText()).isEqualTo(shortSentences);
    }
}
