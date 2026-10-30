package com.acme.retail.rag.ch15;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MultimodalQueryBuilderTest {

    private static final String IMAGE_ID = "customer-upload-4471";
    private static final String DESCRIPTION =
            "Cracked plastic housing on a blender base, visible damage near the power switch.";

    /** Stands in for a real call to Volume 1 Chapter 14's vision-capable ChatClient. */
    private final ImageToTextDescriber stubDescriber = imageId ->
            Map.of(IMAGE_ID, DESCRIPTION).getOrDefault(imageId, "");

    private final MultimodalQueryBuilder queryBuilder = new MultimodalQueryBuilder(stubDescriber);

    @Test
    void combinesTheImageDescriptionWithTheCustomersOwnQuestion() {
        String textQuery = queryBuilder.buildTextQuery(IMAGE_ID, "Is this covered under warranty?");

        assertThat(textQuery).contains(DESCRIPTION);
        assertThat(textQuery).contains("Is this covered under warranty?");
    }

    @Test
    void theDerivedTextQueryIsSomethingOrdinaryKeywordMatchingCanNowSearch() {
        String textQuery = queryBuilder.buildTextQuery(IMAGE_ID, "Is this damage covered?");
        String damageClaimsDocument = "Physical damage to the housing is covered under accidental damage protection.";

        boolean matched = new KeywordMatcher().matches(textQuery, damageClaimsDocument);

        assertThat(matched)
                .as("the photo itself was never searchable -- its derived text description is")
                .isTrue();
    }
}
