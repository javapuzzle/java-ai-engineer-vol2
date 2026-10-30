package com.acme.retail.rag.ch09;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/** Same literal word-matcher from Chapter 2, redeclared so this module stays self-contained. */
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
