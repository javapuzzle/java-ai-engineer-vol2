package com.acme.retail.rag.ch01;

/**
 * The structured knowledge Volume 1's grounding could already check --
 * one row, one product, a fixed set of fields. Nothing here can hold the
 * answer to "is this dishwasher safe" or "what's the return window,"
 * because those answers don't live in a row at all.
 */
public record ProductFact(String sku, String name, boolean inStock, int priceCents) {
}
