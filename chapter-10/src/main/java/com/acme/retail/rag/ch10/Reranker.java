package com.acme.retail.rag.ch10;

import org.springframework.ai.document.Document;

import java.util.List;

public interface Reranker {

    List<Document> rerank(String query, List<Document> initialResults);
}
