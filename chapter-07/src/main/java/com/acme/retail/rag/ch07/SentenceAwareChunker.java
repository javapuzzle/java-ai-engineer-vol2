package com.acme.retail.rag.ch07;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Groups whole sentences into chunks up to a target size, but never splits
 * a single sentence across two chunks -- even if that sentence alone is
 * longer than the target, it still gets its own chunk intact, rather than
 * being truncated or broken mid-thought. Implements the same real Spring
 * AI {@code DocumentTransformer} contract as {@link FixedSizeChunker}, so
 * either chunker can be swapped in wherever a pipeline expects one.
 */
@Service
public class SentenceAwareChunker implements DocumentTransformer {

    private final int targetChunkSize;

    public SentenceAwareChunker(int targetChunkSize) {
        this.targetChunkSize = targetChunkSize;
    }

    @Override
    public List<Document> apply(List<Document> documents) {
        List<Document> chunks = new ArrayList<>();

        for (Document document : documents) {
            chunks.addAll(chunk(document));
        }

        return chunks;
    }

    private List<Document> chunk(Document document) {
        List<String> sentences = splitIntoSentences(document.getText());
        List<Document> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int chunkIndex = 0;

        for (String sentence : sentences) {
            boolean wouldOverflow = current.length() > 0
                    && current.length() + 1 + sentence.length() > targetChunkSize;

            if (wouldOverflow) {
                chunks.add(new Document(document.getId() + "-chunk-" + chunkIndex,
                        current.toString().trim(), document.getMetadata()));
                chunkIndex++;
                current = new StringBuilder();
            }

            if (current.length() > 0) {
                current.append(" ");
            }
            current.append(sentence);
        }

        if (!current.isEmpty()) {
            chunks.add(new Document(document.getId() + "-chunk-" + chunkIndex,
                    current.toString().trim(), document.getMetadata()));
        }

        return chunks;
    }

    private List<String> splitIntoSentences(String text) {
        return Arrays.stream(text.split("(?<=[.!?])\\s+"))
                .map(String::trim)
                .filter(sentence -> !sentence.isBlank())
                .toList();
    }
}
