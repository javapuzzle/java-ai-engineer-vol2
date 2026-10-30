package com.acme.retail.rag.ch19;

import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * A document is stale the moment its source content changes after it was
 * last indexed -- the gap between "the catalog changed" and "the index
 * caught up," which is exactly what silently degrades retrieval quality
 * over time without a single line of code being wrong anywhere.
 */
@Service
public class StaleDocumentDetector {

    public boolean isStale(Instant lastIndexedAt, Instant lastModifiedAt) {
        return lastModifiedAt.isAfter(lastIndexedAt);
    }
}
