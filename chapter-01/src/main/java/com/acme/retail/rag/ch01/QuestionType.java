package com.acme.retail.rag.ch01;

/**
 * Every customer question falls into one of these two shapes. This
 * chapter's whole point is that Acme Retail's codebase, as of Launch Day,
 * can only ever resolve one of them.
 */
public enum QuestionType {

    /** Settleable by one lookup against a known row -- Volume 1's grounding. */
    STRUCTURED_FACT,

    /** Only answerable by reading a document nothing in this codebase indexes yet. */
    DOCUMENT_KNOWLEDGE
}
