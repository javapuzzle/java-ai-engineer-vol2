package com.acme.retail.rag.ch20;

/** The three shapes of knowledge the capstone assistant might need to consult. */
public enum QueryRoute {

    /** Settleable by one row lookup -- Chapter 1's structured grounding. */
    STRUCTURED_FACT_LOOKUP,

    /** Needs this volume's document retrieval pipeline. */
    DOCUMENT_RAG,

    /** Needs a live external system (like an order-tracking API) -- not built in this volume. */
    EXTERNAL_TOOL_CALL
}
