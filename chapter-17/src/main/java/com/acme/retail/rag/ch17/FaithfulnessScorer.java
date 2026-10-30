package com.acme.retail.rag.ch17;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Measures GENERATION against RETRIEVAL: of the sentences in the generated
 * answer, what fraction are actually traceable to the retrieved context?
 * This is Chapter 16's {@code FaithfulnessChecker} idea restated as a
 * score instead of a list -- the same word-overlap heuristic standing in
 * for a real LLM-judged faithfulness metric.
 */
@Service
public class FaithfulnessScorer {

    private static final Set<String> STOPWORDS = Set.of(
            "i", "a", "the", "is", "it", "to", "for", "of", "and", "on", "in", "with", "also",
            "this", "that", "as", "at", "by"
    );
    private static final double MINIMUM_OVERLAP_RATIO = 0.4;

    public double score(GoldenCase goldenCase) {
        Set<String> contextWords = goldenCase.retrievedContext().stream()
                .flatMap(chunk -> tokenize(chunk).stream())
                .collect(Collectors.toSet());

        List<String> sentences = splitIntoSentences(goldenCase.generatedAnswer());
        if (sentences.isEmpty()) {
            return 1.0;
        }

        long groundedCount = sentences.stream().filter(sentence -> isGrounded(sentence, contextWords)).count();

        return (double) groundedCount / sentences.size();
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
