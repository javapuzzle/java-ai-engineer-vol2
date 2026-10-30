package com.acme.retail.rag.ch19;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class StaleDocumentDetectorTest {

    private final StaleDocumentDetector detector = new StaleDocumentDetector();

    @Test
    void flagsADocumentAsStaleWhenItWasModifiedAfterItWasLastIndexed() {
        Instant lastIndexedAt = Instant.parse("2026-01-01T00:00:00Z");
        Instant lastModifiedAt = Instant.parse("2026-01-15T00:00:00Z");

        assertThat(detector.isStale(lastIndexedAt, lastModifiedAt)).isTrue();
    }

    @Test
    void doesNotFlagADocumentThatHasNotChangedSinceItWasIndexed() {
        Instant lastIndexedAt = Instant.parse("2026-01-15T00:00:00Z");
        Instant lastModifiedAt = Instant.parse("2026-01-01T00:00:00Z");

        assertThat(detector.isStale(lastIndexedAt, lastModifiedAt)).isFalse();
    }

    @Test
    void doesNotFlagADocumentIndexedAtTheExactMomentItWasLastModified() {
        Instant sameInstant = Instant.parse("2026-01-01T00:00:00Z");

        assertThat(detector.isStale(sameInstant, sameInstant)).isFalse();
    }
}
