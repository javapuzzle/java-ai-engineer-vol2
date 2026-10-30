package com.acme.retail.rag.ch12;

import java.util.List;

/**
 * The outcome of a corrective pass over retrieval results. {@code
 * answerable} is false when every candidate chunk failed grading -- the
 * honest, deliberate alternative to quoting an irrelevant chunk as if it
 * applied.
 */
public record CorrectiveRagResult(boolean answerable, List<String> relevantChunkTexts, String message) {

    public static CorrectiveRagResult unanswerable() {
        return new CorrectiveRagResult(false, List.of(),
                "I don't have enough relevant information to answer that confidently.");
    }

    public static CorrectiveRagResult answerable(List<String> relevantChunkTexts) {
        return new CorrectiveRagResult(true, relevantChunkTexts, null);
    }
}
