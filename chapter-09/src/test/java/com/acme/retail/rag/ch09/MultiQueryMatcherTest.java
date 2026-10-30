package com.acme.retail.rag.ch09;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MultiQueryMatcherTest {

    private final MultiQueryMatcher multiQueryMatcher =
            new MultiQueryMatcher(new QueryExpander(), new KeywordMatcher());
    private final KeywordMatcher plainMatcher = new KeywordMatcher();

    private static final String RETURN_POLICY_DOCUMENT =
            "Items may be returned within 30 days of purchase for a full refund. "
                    + "To start a return, contact support.";

    @Test
    void theOriginalPhrasingAloneStillFailsPlainKeywordMatching() {
        String colloquialQuestion = "Can I send back the blender I bought last week?";

        assertThat(plainMatcher.matches(colloquialQuestion, RETURN_POLICY_DOCUMENT)).isFalse();
    }

    @Test
    void expandingTheSameQuestionFirstLetsMultiQueryMatchingSucceed() {
        String colloquialQuestion = "Can I send back the blender I bought last week?";

        assertThat(multiQueryMatcher.matchesAnyVariant(colloquialQuestion, RETURN_POLICY_DOCUMENT)).isTrue();
    }

    @Test
    void aQuestionThatAlreadyUsesCanonicalVocabularyMatchesWithoutNeedingExpansion() {
        String canonicalQuestion = "What is your return policy?";

        assertThat(multiQueryMatcher.matchesAnyVariant(canonicalQuestion, RETURN_POLICY_DOCUMENT)).isTrue();
    }
}
