package com.acme.retail.rag.ch02;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordMatcherTest {

    private final KeywordMatcher matcher = new KeywordMatcher();

    @Test
    void matchesWhenQuestionSharesAnExactWordWithTheDocument() {
        boolean result = matcher.matches(
                "Is this in stock?",
                "Stock status: yes, currently in stock.");

        assertThat(result).isTrue();
    }

    @Test
    void failsOnAParaphraseThatSharesNoLiteralWords() {
        boolean result = matcher.matches(
                "Can I send back the blender I bought last week?",
                "Items may be returned within 30 days of purchase for a full refund.");

        assertThat(result).isFalse();
    }
}
