package com.acme.retail.rag.ch12;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Grades every candidate chunk before trusting any of them. Chunks that
 * fail grading are dropped, never passed to generation -- if nothing
 * survives, the service returns an honest "I don't know" result instead
 * of letting {@code AugmentedPromptBuilder} (Chapter 8) faithfully quote
 * an irrelevant chunk as if it applied.
 */
@Service
public class CorrectiveRagService {

    private final RelevanceGrader grader;

    public CorrectiveRagService(RelevanceGrader grader) {
        this.grader = grader;
    }

    public CorrectiveRagResult respond(String query, List<String> retrievedChunkTexts) {
        List<String> relevantChunks = retrievedChunkTexts.stream()
                .filter(chunkText -> grader.grade(query, chunkText) == RelevanceGrade.RELEVANT)
                .toList();

        return relevantChunks.isEmpty()
                ? CorrectiveRagResult.unanswerable()
                : CorrectiveRagResult.answerable(relevantChunks);
    }
}
