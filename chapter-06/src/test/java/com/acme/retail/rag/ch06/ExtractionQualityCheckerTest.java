package com.acme.retail.rag.ch06;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExtractionQualityCheckerTest {

    private final ExtractionQualityChecker checker = new ExtractionQualityChecker();

    @Test
    void flagsReadableSentencesAsClean() {
        String extracted = "Items may be returned within 30 days of purchase for a full refund.";

        assertThat(checker.check(extracted)).isEqualTo(ExtractionQuality.CLEAN);
    }

    @Test
    void flagsMostlySymbolGarbageAsSuspect() {
        String garbled = "!@# $%^ &*( )_+ {}| :<> ?~` -=[ ];', ./1 234 567 890";

        assertThat(checker.check(garbled)).isEqualTo(ExtractionQuality.SUSPECT);
    }

    @Test
    void flagsSuspiciouslyShortExtractionsAsSuspect() {
        String tooShort = "Hi.";

        assertThat(checker.check(tooShort)).isEqualTo(ExtractionQuality.SUSPECT);
    }

    @Test
    void flagsEmptyExtractionAsSuspect() {
        assertThat(checker.check("")).isEqualTo(ExtractionQuality.SUSPECT);
    }
}
