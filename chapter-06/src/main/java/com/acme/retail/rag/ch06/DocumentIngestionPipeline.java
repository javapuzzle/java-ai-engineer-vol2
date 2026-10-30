package com.acme.retail.rag.ch06;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Cleans a raw extraction and flags it for review instead of silently
 * indexing it -- the pipeline never drops a suspect document, because a
 * human deciding "this needs re-extraction" is a very different outcome
 * than a document quietly vanishing from the knowledge base with no trace.
 *
 * <p>The output is a real Spring AI {@link Document} (from
 * {@code spring-ai-commons}), not a custom result type -- this is the
 * actual object every later chapter's chunking, embedding, and retrieval
 * code expects to receive. The quality verdict travels as metadata, since
 * {@code Document}'s metadata map is exactly where a real ingestion
 * pipeline would attach a fact like this.
 */
@Service
public class DocumentIngestionPipeline {

    static final String QUALITY_METADATA_KEY = "extractionQuality";

    private final DocumentCleaner cleaner;
    private final ExtractionQualityChecker qualityChecker;

    public DocumentIngestionPipeline(DocumentCleaner cleaner, ExtractionQualityChecker qualityChecker) {
        this.cleaner = cleaner;
        this.qualityChecker = qualityChecker;
    }

    public Document load(String fileName, String rawExtractedText) {
        String cleanedText = cleaner.clean(rawExtractedText);
        ExtractionQuality quality = qualityChecker.check(cleanedText);

        return new Document(fileName, cleanedText, Map.of(QUALITY_METADATA_KEY, quality.name()));
    }
}
