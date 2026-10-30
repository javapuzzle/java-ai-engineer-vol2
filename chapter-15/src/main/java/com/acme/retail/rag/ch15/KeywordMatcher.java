package com.acme.retail.rag.ch15;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/** Same literal word-matcher from Chapter 2, redeclared so this module stays self-contained. */
public class KeywordMatcher {

    public boolean matches(String question, String document) {
        Set<String> documentWords = tokenize(document);
        Set<String> questionWords = tokenize(question);

        return questionWords.stream().anyMatch(documentWords::contains);
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-zA-Z0-9À-ſ]+"))
                .filter(word -> !word.isBlank())
                .collect(Collectors.toSet());
    }
}
