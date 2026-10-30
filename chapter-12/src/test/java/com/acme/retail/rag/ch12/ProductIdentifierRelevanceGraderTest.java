package com.acme.retail.rag.ch12;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductIdentifierRelevanceGraderTest {

    private final ProductIdentifierRelevanceGrader grader = new ProductIdentifierRelevanceGrader();

    private static final String QUERY = "What is the warranty period for the ProBlend500?";

    @Test
    void gradesAChunkAboutADifferentNamedModelAsIrrelevant() {
        String wrongModelChunk =
                "The ProBlend900 comes with a two-year manufacturer's warranty covering defects "
                        + "in materials and workmanship.";

        assertThat(grader.grade(QUERY, wrongModelChunk)).isEqualTo(RelevanceGrade.IRRELEVANT);
    }

    @Test
    void gradesAChunkAboutTheSameNamedModelAsRelevant() {
        String correctModelChunk =
                "The ProBlend500 was discontinued in 2019 and is no longer covered under active warranty support.";

        assertThat(grader.grade(QUERY, correctModelChunk)).isEqualTo(RelevanceGrade.RELEVANT);
    }

    @Test
    void defersToRetrievalWhenTheQuestionNamesNoSpecificModel() {
        String genericQuestion = "What is your general warranty policy?";
        String anyChunk = "Most Acme Retail products include a standard one-year warranty.";

        assertThat(grader.grade(genericQuestion, anyChunk)).isEqualTo(RelevanceGrade.RELEVANT);
    }
}
