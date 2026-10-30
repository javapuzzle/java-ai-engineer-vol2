package com.acme.retail.rag.ch20;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Grades every permission-cleared candidate before trusting any of it --
 * Chapter 12's Corrective RAG lesson, generalized from product-model
 * matching to plain meaningful-word overlap, since the capstone spans
 * product manuals, policies, and support docs rather than one product
 * catalog. A chunk that shares no meaningful term with the question is
 * dropped, never handed to prompt construction.
 */
@Service
public class CorrectiveGradingService {

    private static final Set<String> STOPWORDS = Set.of(
            "i", "a", "the", "is", "it", "to", "for", "of", "can", "do", "does", "this", "that",
            "my", "me", "in", "if", "an", "what", "how", "on", "are", "you", "your"
    );

    public List<Document> gradeRelevant(String query, List<Document> candidates) {
        Set<String> queryWords = tokenize(query);

        return candidates.stream()
                .filter(document -> sharesAtLeastOneMeaningfulWord(queryWords, document.getText()))
                .toList();
    }

    private boolean sharesAtLeastOneMeaningfulWord(Set<String> queryWords, String chunkText) {
        return tokenize(chunkText).stream().anyMatch(queryWords::contains);
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(word -> !word.isBlank())
                .filter(word -> !STOPWORDS.contains(word))
                .collect(Collectors.toSet());
    }
}
