package com.acme.retail.rag.ch15;

/**
 * Combines an image's derived text description with the customer's own
 * words into one text query -- the point where "multimodal" input rejoins
 * the ordinary text retrieval pipeline every other chapter already built.
 */
public class MultimodalQueryBuilder {

    private final ImageToTextDescriber describer;

    public MultimodalQueryBuilder(ImageToTextDescriber describer) {
        this.describer = describer;
    }

    public String buildTextQuery(String imageId, String customerQuestion) {
        String description = describer.describe(imageId);

        return "Image shows: " + description + ". Customer question: " + customerQuestion;
    }
}
