package com.acme.retail.rag.ch07;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * The naive approach, implementing the real Spring AI {@code
 * DocumentTransformer} contract -- the same interface a real text
 * splitter (like {@code TokenTextSplitter}) implements. Cuts each
 * document's text every {@code chunkSize} characters, with no awareness of
 * where a sentence -- or a word -- happens to be at that boundary. Cheap
 * and simple, and guaranteed to occasionally slice straight through the
 * one sentence that actually answers a question.
 */
@Service
public class FixedSizeChunker implements DocumentTransformer {

    private final int chunkSize;

    public FixedSizeChunker(int chunkSize) {
        this.chunkSize = chunkSize;
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
        String text = document.getText();
        List<Document> chunks = new ArrayList<>();
        int chunkIndex = 0;

        for (int start = 0; start < text.length(); start += chunkSize) {
            int end = Math.min(start + chunkSize, text.length());
            String chunkText = text.substring(start, end);
            chunks.add(new Document(document.getId() + "-chunk-" + chunkIndex, chunkText, document.getMetadata()));
            chunkIndex++;
        }

        return chunks;
    }
}
