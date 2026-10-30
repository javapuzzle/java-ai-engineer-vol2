package com.acme.retail.rag.ch17;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Measures GENERATION against the QUESTION, not the context: does the
 * answer actually address what was asked? This is deliberately a
 * different axis than faithfulness -- an answer can be perfectly grounded
 * in some retrieved chunk and still fail to address the question that was
 * actually asked, if retrieval handed generation the wrong chunk.
 */
@Service
public class AnswerRelevanceScorer {

    private static final Set<String> STOPWORDS = Set.of(
            "i", "a", "the", "is", "it", "to", "for", "of", "and", "on", "in", "with", "also",
            "this", "that", "as", "at", "by", "what", "does", "do", "can"
    );

    public double score(GoldenCase goldenCase) {
        Set<String> questionWords = tokenize(goldenCase.question());
        if (questionWords.isEmpty()) {
            return 1.0;
        }

        Set<String> answerWords = tokenize(goldenCase.generatedAnswer());
        long overlapCount = questionWords.stream().filter(answerWords::contains).count();

        return (double) overlapCount / questionWords.size();
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(word -> !word.isBlank())
                .filter(word -> !STOPWORDS.contains(word))
                .collect(Collectors.toSet());
    }
}
