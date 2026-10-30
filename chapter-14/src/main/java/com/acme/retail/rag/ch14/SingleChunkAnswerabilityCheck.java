package com.acme.retail.rag.ch14;

import org.springframework.ai.document.Document;

import java.util.Locale;

/**
 * Checks whether a single flat chunk, on its own, contains enough text to
 * answer a compound question -- one that depends on two separate facts
 * (coverage AND eligibility). This exists to make the contrast with
 * {@link GraphQueryService} concrete: no single chunk needs to actually
 * contain both facts for the check to prove the point. Operates on real
 * Spring AI {@link Document} instances (from {@code spring-ai-commons}).
 */
public class SingleChunkAnswerabilityCheck {

    public boolean containsBothFacts(Document chunk, String... requiredPhrases) {
        String normalized = chunk.getText().toLowerCase(Locale.ROOT);

        for (String phrase : requiredPhrases) {
            if (!normalized.contains(phrase.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }
}
