package com.acme.retail.rag.ch11;

/** A document's position in one ranked list -- 1 is the best match, per that one search method. */
public record RankedDocument(String documentId, int rank) {
}
