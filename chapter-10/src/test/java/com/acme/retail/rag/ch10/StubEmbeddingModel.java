package com.acme.retail.rag.ch10;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.Map;

/**
 * A hand-written test double for {@code EmbeddingModel} -- Mockito's
 * inline mock maker cannot self-attach in this build environment (it
 * requires a dynamic agent attachment that this sandbox blocks), so every
 * chapter in this volume that needs to stand in for a real model call uses
 * a small class like this one instead, implementing the real interface
 * directly rather than mocking it.
 */
class StubEmbeddingModel implements EmbeddingModel {

    private final Map<String, float[]> embeddingsByText;

    StubEmbeddingModel(Map<String, float[]> embeddingsByText) {
        this.embeddingsByText = embeddingsByText;
    }

    @Override
    public float[] embed(String text) {
        float[] result = embeddingsByText.get(text);
        if (result == null) {
            throw new IllegalStateException("No hand-placed embedding stubbed for: " + text);
        }
        return result;
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getText());
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        throw new UnsupportedOperationException("Not used by this chapter's tests");
    }
}
