package com.acme.retail.rag.ch16;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks a generated answer AFTER the fact, sentence by sentence, against
 * the context it was supposed to be grounded in. This is the output-side
 * complement to Chapter 12's Corrective RAG, which grades chunks BEFORE
 * generation -- this class catches a sentence the model added on its own,
 * even when every retrieved chunk was perfectly relevant.
 *
 * <p>Real faithfulness checking uses an LLM judge asking "is this sentence
 * supported by this context, yes or no" per sentence. This chapter's
 * version uses word overlap as a deterministic, testable stand-in -- the
 * same simplification used for grading and reranking throughout this
 * volume.
 */
@Service
public class FaithfulnessChecker {

    private static final Set<String> STOPWORDS = Set.of(
            "i", "a", "the", "is", "it", "to", "for", "of", "and", "on", "in", "with", "also",
            "this", "that", "as", "at", "by"
    );
    private static final double MINIMUM_OVERLAP_RATIO = 0.4;

    public List<String> findUngroundedSentences(String answer, List<String> contextChunks) {
        Set<String> contextWords = contextChunks.stream()
                .flatMap(chunk -> tokenize(chunk).stream())
                .collect(Collectors.toSet());

        return splitIntoSentences(answer).stream()
                .filter(sentence -> !isGrounded(sentence, contextWords))
                .toList();
    }

    private boolean isGrounded(String sentence, Set<String> contextWords) {
        Set<String> sentenceWords = tokenize(sentence);
        if (sentenceWords.isEmpty()) {
            return true;
        }

        long overlapCount = sentenceWords.stream().filter(contextWords::contains).count();
        double overlapRatio = (double) overlapCount / sentenceWords.size();

        return overlapRatio >= MINIMUM_OVERLAP_RATIO;
    }

    private List<String> splitIntoSentences(String text) {
        return Arrays.stream(text.split("(?<=[.!?])\\s+"))
                .map(String::trim)
                .filter(sentence -> !sentence.isBlank())
                .toList();
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(word -> !word.isBlank())
                .filter(word -> !STOPWORDS.contains(word))
                .collect(Collectors.toSet());
    }
}
