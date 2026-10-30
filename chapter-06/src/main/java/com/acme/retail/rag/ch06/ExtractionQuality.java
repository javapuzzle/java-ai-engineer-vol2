package com.acme.retail.rag.ch06;

/**
 * Whether a cleaned extraction looks like real, readable text or looks
 * like it came from a broken extraction (a scanned image OCR'd badly, a
 * mangled table, a corrupted PDF stream).
 */
public enum ExtractionQuality {
    CLEAN,
    SUSPECT
}
