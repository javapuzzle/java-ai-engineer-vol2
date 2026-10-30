package com.acme.retail.rag.ch10;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A stand-in for a real cross-encoder reranker (which looks at the query
 * and a document together and outputs a single relevance score). This
 * illustrative version counts exact meaningful-word overlap instead --
 * cheaper and cruder than a real cross-encoder, but enough to demonstrate
 * why a second, more query-aware pass can correct an embedding-only
 * ranking's mistake.
 */
@Service
public class KeywordOverlapReranker implements Reranker {

    private static final Set<String> STOPWORDS = Set.of(
            "i", "a", "the", "is", "it", "to", "for", "of", "can", "do", "does", "this", "that",
            "my", "me", "in", "if", "an"
    );

    @Override
    public List<Document> rerank(String query, List<Document> initialResults) {
        Set<String> queryWords = tokenize(query);

        return initialResults.stream()
                .sorted(Comparator.comparingLong((Document document) -> overlapCount(queryWords, document.getText()))
                        .reversed())
                .toList();
    }

    private long overlapCount(Set<String> queryWords, String documentText) {
        Set<String> documentWords = tokenize(documentText);
        return queryWords.stream().filter(documentWords::contains).count();
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(word -> !word.isBlank())
                .filter(word -> !STOPWORDS.contains(word))
                .collect(Collectors.toSet());
    }
}
