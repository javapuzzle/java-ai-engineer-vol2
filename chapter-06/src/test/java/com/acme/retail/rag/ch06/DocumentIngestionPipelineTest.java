package com.acme.retail.rag.ch06;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentIngestionPipelineTest {

    private final DocumentIngestionPipeline pipeline =
            new DocumentIngestionPipeline(new DocumentCleaner(), new ExtractionQualityChecker());

    @Test
    void loadsAWellFormedDocumentAsClean() {
        String raw = "  Items   may be returned\n\nwithin 30 days of purchase.  ";

        Document result = pipeline.load("return-policy.pdf", raw);

        assertThat(result.getText()).isEqualTo("Items may be returned within 30 days of purchase.");
        assertThat(result.getMetadata().get(DocumentIngestionPipeline.QUALITY_METADATA_KEY))
                .isEqualTo(ExtractionQuality.CLEAN.name());
    }

    @Test
    void flagsABadlyExtractedDocumentAsSuspectWithoutDroppingItsText() {
        String garbled = "!@# $%^ &*( )_+ {}| :<> ?~` -=[ ];', ./1 234 567 890";

        Document result = pipeline.load("scanned-warranty.pdf", garbled);

        assertThat(result.getMetadata().get(DocumentIngestionPipeline.QUALITY_METADATA_KEY))
                .isEqualTo(ExtractionQuality.SUSPECT.name());
        assertThat(result.getText()).isNotBlank();
    }
}
