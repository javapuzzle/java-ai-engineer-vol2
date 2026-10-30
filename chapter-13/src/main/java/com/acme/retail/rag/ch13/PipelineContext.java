package com.acme.retail.rag.ch13;

import java.util.ArrayList;
import java.util.List;

/**
 * The shared state every stage reads from and writes to. Stages don't call
 * each other directly and don't know which stages ran before or after
 * them -- they only know this context's shape. That's what makes the
 * pipeline a swappable list instead of a hardcoded call chain.
 */
public class PipelineContext {

    private final String originalQuestion;
    private List<String> retrievedChunks = new ArrayList<>();
    private List<String> chunksForPrompt = new ArrayList<>();
    private String finalPrompt;

    public PipelineContext(String originalQuestion) {
        this.originalQuestion = originalQuestion;
    }

    public String getOriginalQuestion() {
        return originalQuestion;
    }

    public List<String> getRetrievedChunks() {
        return retrievedChunks;
    }

    public void setRetrievedChunks(List<String> retrievedChunks) {
        this.retrievedChunks = retrievedChunks;
    }

    public List<String> getChunksForPrompt() {
        return chunksForPrompt;
    }

    public void setChunksForPrompt(List<String> chunksForPrompt) {
        this.chunksForPrompt = chunksForPrompt;
    }

    public String getFinalPrompt() {
        return finalPrompt;
    }

    public void setFinalPrompt(String finalPrompt) {
        this.finalPrompt = finalPrompt;
    }
}
