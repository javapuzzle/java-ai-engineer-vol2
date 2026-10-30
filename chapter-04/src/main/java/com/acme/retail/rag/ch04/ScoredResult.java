package com.acme.retail.rag.ch04;

public record ScoredResult(String documentId, double similarity) {
}
