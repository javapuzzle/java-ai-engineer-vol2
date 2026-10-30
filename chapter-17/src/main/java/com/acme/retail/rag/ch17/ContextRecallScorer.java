package com.acme.retail.rag.ch17;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Measures RETRIEVAL, not generation: of the facts a correct answer would
 * need, how many did retrieval actually surface? A low recall score points
 * at chunking, embedding, or the vector store -- never at the model call,
 * since generation cannot use a fact retrieval never handed it.
 */
@Service
public class ContextRecallScorer {

    public double score(GoldenCase goldenCase) {
        String combinedContext = String.join(" ", goldenCase.retrievedContext()).toLowerCase(Locale.ROOT);
        List<String> requiredFacts = goldenCase.requiredContextFacts();

        if (requiredFacts.isEmpty()) {
            return 1.0;
        }

        long foundCount = requiredFacts.stream()
                .filter(fact -> combinedContext.contains(fact.toLowerCase(Locale.ROOT)))
                .count();

        return (double) foundCount / requiredFacts.size();
    }
}
