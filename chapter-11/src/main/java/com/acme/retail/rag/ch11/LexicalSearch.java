package com.acme.retail.rag.ch11;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simplified, illustrative stand-in for BM25 (lexical/keyword) search.
 * Real BM25 weighs a term by how rare it is across the whole corpus (its
 * inverse document frequency) -- a term appearing in almost every document
 * contributes little, a term appearing in almost none is highly
 * distinctive. This chapter hand-assigns those weights directly, since
 * computing real IDF needs a real corpus statistic this toy example
 * doesn't have; the ranking behavior it produces is the same in kind.
 * Operates on real Spring AI {@link Document} instances.
 */
@Service
public class LexicalSearch {

    private final Map<String, Double> termRarityWeights;

    public LexicalSearch(Map<String, Double> termRarityWeights) {
        this.termRarityWeights = termRarityWeights;
    }

    public List<RankedDocument> rank(String query, List<Document> documents) {
        Set<String> queryTerms = tokenize(query);

        List<Document> sorted = documents.stream()
                .sorted(Comparator.comparingDouble((Document doc) -> score(queryTerms, doc.getText())).reversed())
                .toList();

        return toRankedList(sorted);
    }

    private double score(Set<String> queryTerms, String documentText) {
        Set<String> documentTerms = tokenize(documentText);

        return queryTerms.stream()
                .filter(documentTerms::contains)
                .mapToDouble(term -> termRarityWeights.getOrDefault(term, 0.0))
                .sum();
    }

    private List<RankedDocument> toRankedList(List<Document> sortedDocuments) {
        return java.util.stream.IntStream.range(0, sortedDocuments.size())
                .mapToObj(i -> new RankedDocument(sortedDocuments.get(i).getId(), i + 1))
                .toList();
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(word -> !word.isBlank())
                .collect(Collectors.toSet());
    }
}
