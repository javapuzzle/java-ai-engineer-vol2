package com.acme.retail.rag.ch06;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentCleanerTest {

    private final DocumentCleaner cleaner = new DocumentCleaner();

    @Test
    void collapsesMixedWhitespaceIntoSingleSpaces() {
        String raw = "Items   may\n\nbe   returned\t\twithin  30 days.";

        assertThat(cleaner.clean(raw)).isEqualTo("Items may be returned within 30 days.");
    }

    @Test
    void collapsesRepeatedInternalSpacesFromBadExtraction() {
        String raw = "30      days      remaining.";

        assertThat(cleaner.clean(raw)).isEqualTo("30 days remaining.");
    }

    @Test
    void trimsLeadingAndTrailingWhitespace() {
        String raw = "   Full refund available.   ";

        assertThat(cleaner.clean(raw)).isEqualTo("Full refund available.");
    }
}
