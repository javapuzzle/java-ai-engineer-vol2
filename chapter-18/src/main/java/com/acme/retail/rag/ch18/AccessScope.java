package com.acme.retail.rag.ch18;

/** Who a document is cleared for -- checked before retrieval ever hands a chunk to a prompt. */
public enum AccessScope {
    PUBLIC,
    INTERNAL_SUPPORT,
    SENIOR_STAFF_ONLY
}
