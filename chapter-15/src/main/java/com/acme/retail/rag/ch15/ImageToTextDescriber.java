package com.acme.retail.rag.ch15;

/**
 * The seam where a real vision-capable model plugs in -- the same
 * multimodal {@code ChatClient} call Volume 1 Chapter 14 already built and
 * proved. Multimodal RAG's practical shape is not a separate retrieval
 * architecture for images: it's converting the image to text once, here,
 * and then handing that text to the exact same retrieval pipeline every
 * other chapter already built.
 */
public interface ImageToTextDescriber {

    String describe(String imageId);
}
