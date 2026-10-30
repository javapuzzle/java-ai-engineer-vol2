package com.acme.retail.rag.ch09;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QueryExpanderTest {

    private final QueryExpander expander = new QueryExpander();

    @Test
    void alwaysIncludesTheOriginalQueryUnchanged() {
        String original = "Can I send back the blender I bought last week?";

        List<String> variants = expander.expand(original);

        assertThat(variants).contains(original);
    }

    @Test
    void addsACanonicalVocabularyVariantWhenAColloquialPhraseIsDetected() {
        String original = "Can I send back the blender I bought last week?";

        List<String> variants = expander.expand(original);

        assertThat(variants).contains("Can I return the blender I bought last week?");
    }

    @Test
    void addsNoExtraVariantsWhenNoColloquialPhraseIsDetected() {
        String original = "What is the warranty period for this product?";

        List<String> variants = expander.expand(original);

        assertThat(variants).containsExactly(original);
    }

    @Test
    void matchingIsCaseInsensitive() {
        String original = "Can I SEND BACK this item?";

        List<String> variants = expander.expand(original);

        assertThat(variants).anyMatch(variant -> variant.toLowerCase().contains("return"));
    }
}
