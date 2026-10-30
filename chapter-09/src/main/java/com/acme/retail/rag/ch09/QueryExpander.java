package com.acme.retail.rag.ch09;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Rewrites a user's literal question into one or more canonical-vocabulary
 * variants before retrieval ever runs. A real system uses an LLM call for
 * this; this chapter's version uses a small, explicit phrase map so the
 * rewriting logic itself can be tested deterministically, without a
 * network call. The original query is always kept alongside its variants
 * -- expansion adds candidates, it never removes the user's actual words.
 */
@Service
public class QueryExpander {

    private static final Map<String, String> COLLOQUIAL_TO_CANONICAL = new LinkedHashMap<>();

    static {
        COLLOQUIAL_TO_CANONICAL.put("send back", "return");
        COLLOQUIAL_TO_CANONICAL.put("give back", "return");
        COLLOQUIAL_TO_CANONICAL.put("cancel my order", "cancel the order");
    }

    public List<String> expand(String originalQuery) {
        List<String> variants = new ArrayList<>();
        variants.add(originalQuery);

        String normalizedQuery = originalQuery.toLowerCase(Locale.ROOT);

        for (Map.Entry<String, String> entry : COLLOQUIAL_TO_CANONICAL.entrySet()) {
            String colloquialPhrase = entry.getKey();
            String canonicalPhrase = entry.getValue();

            if (normalizedQuery.contains(colloquialPhrase)) {
                String rewritten = originalQuery.replaceAll(
                        "(?i)" + Pattern.quote(colloquialPhrase), canonicalPhrase);
                variants.add(rewritten);
            }
        }

        return variants;
    }
}
