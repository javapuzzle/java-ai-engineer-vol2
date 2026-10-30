package com.acme.retail.rag.ch10;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The complete two-stage pattern: retrieve broadly and cheaply by
 * embedding similarity first, then rerank the smaller candidate set with a
 * more precise (and more expensive) relevance judgment. Reranking only the
 * top handful of candidates -- not the whole corpus -- is what keeps the
 * expensive step affordable.
 */
@Service
public class TwoStageRetrieval {

    private final InitialRetriever initialRetriever;
    private final Reranker reranker;

    public TwoStageRetrieval(InitialRetriever initialRetriever, Reranker reranker) {
        this.initialRetriever = initialRetriever;
        this.reranker = reranker;
    }

    public List<Document> search(String query) {
        List<Document> initialResults = initialRetriever.retrieve(query);
        return reranker.rerank(query, initialResults);
    }
}
