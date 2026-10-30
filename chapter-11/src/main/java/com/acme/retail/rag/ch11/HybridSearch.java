package com.acme.retail.rag.ch11;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Fuses a lexical ranking and a dense ranking with Reciprocal Rank Fusion
 * (RRF): each document's fused score is the sum of {@code 1/(k + rank)}
 * across every ranking it appears in. A document missing from one ranking
 * simply contributes nothing from that ranking, rather than being
 * penalized further -- it isn't double-punished for the search method that
 * didn't find it.
 *
 * <p>Real systems commonly use k=60, chosen so a huge corpus's rank-1
 * result doesn't overwhelm every other signal. This chapter uses a smaller
 * k so the arithmetic stays legible for a three-document illustration --
 * the fusion mechanism is identical either way, only the constant differs.
 */
@Service
public class HybridSearch {

    private final LexicalSearch lexicalSearch;
    private final DenseSearch denseSearch;
    private final double k;

    public HybridSearch(LexicalSearch lexicalSearch, DenseSearch denseSearch, double k) {
        this.lexicalSearch = lexicalSearch;
        this.denseSearch = denseSearch;
        this.k = k;
    }

    public List<String> search(String query, List<Document> documents) {
        Map<String, Integer> lexicalRankById = toRankMap(lexicalSearch.rank(query, documents));
        Map<String, Integer> denseRankById = toRankMap(denseSearch.rank(query, documents));

        return documents.stream()
                .map(Document::getId)
                .sorted(Comparator.comparingDouble(
                        (String id) -> fusedScore(id, lexicalRankById, denseRankById)).reversed())
                .toList();
    }

    private double fusedScore(String documentId,
                               Map<String, Integer> lexicalRankById,
                               Map<String, Integer> denseRankById) {
        double score = 0.0;

        if (lexicalRankById.containsKey(documentId)) {
            score += 1.0 / (k + lexicalRankById.get(documentId));
        }
        if (denseRankById.containsKey(documentId)) {
            score += 1.0 / (k + denseRankById.get(documentId));
        }

        return score;
    }

    private Map<String, Integer> toRankMap(List<RankedDocument> rankedDocuments) {
        return rankedDocuments.stream()
                .collect(Collectors.toMap(RankedDocument::documentId, RankedDocument::rank));
    }
}
