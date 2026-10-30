package com.acme.retail.rag.ch02;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The approach Sarah tries first: does any non-trivial word in the question
 * literally appear, word-for-word, in the document? This is exactly the
 * failure mode retrieval has to solve -- it works for exact vocabulary
 * matches and fails completely on paraphrase, because it has no notion of
 * "means the same thing," only "is the same characters."
 */
public class KeywordMatcher {

    private static final Set<String> STOPWORDS = Set.of(
            "i", "a", "the", "is", "it", "to", "for", "of", "can", "do", "does", "this", "that", "my", "me", "in"
    );

    public boolean matches(String question, String document) {
        Set<String> documentWords = tokenize(document);

        return tokenize(question).stream()
                .filter(word -> !STOPWORDS.contains(word))
                .anyMatch(documentWords::contains);
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(word -> !word.isBlank())
                .collect(Collectors.toSet());
    }
}
