package com.acme.retail.rag.ch06;

import org.springframework.stereotype.Service;

/**
 * A cheap, honest signal for "did extraction probably work," checked before
 * a chunk is ever trusted enough to embed. Real extraction failures --
 * scanned images run through bad OCR, corrupted PDF text streams -- tend to
 * produce text that is mostly punctuation and symbols rather than letters.
 * This is not a proof of quality, only a low-cost trip wire.
 */
@Service
public class ExtractionQualityChecker {

    private static final int MINIMUM_LENGTH = 20;
    private static final double MINIMUM_LETTER_RATIO = 0.5;

    public ExtractionQuality check(String cleanedText) {
        if (cleanedText.length() < MINIMUM_LENGTH) {
            return ExtractionQuality.SUSPECT;
        }

        long letterCount = cleanedText.chars().filter(Character::isLetter).count();
        long nonWhitespaceCount = cleanedText.chars().filter(c -> !Character.isWhitespace(c)).count();

        if (nonWhitespaceCount == 0) {
            return ExtractionQuality.SUSPECT;
        }

        double letterRatio = (double) letterCount / nonWhitespaceCount;
        return letterRatio >= MINIMUM_LETTER_RATIO ? ExtractionQuality.CLEAN : ExtractionQuality.SUSPECT;
    }
}
