package com.acme.retail.rag.ch09;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Multi-query retrieval: instead of trusting one phrasing of the question,
 * try every expanded variant and treat a match on ANY of them as a hit.
 * This is what turns query rewriting from an interesting idea into an
 * actual retrieval-quality fix.
 */
@Service
public class MultiQueryMatcher {

    private final QueryExpander expander;
    private final KeywordMatcher matcher;

    public MultiQueryMatcher(QueryExpander expander, KeywordMatcher matcher) {
        this.expander = expander;
        this.matcher = matcher;
    }

    public boolean matchesAnyVariant(String originalQuery, String document) {
        List<String> variants = expander.expand(originalQuery);

        return variants.stream().anyMatch(variant -> matcher.matches(variant, document));
    }
}
