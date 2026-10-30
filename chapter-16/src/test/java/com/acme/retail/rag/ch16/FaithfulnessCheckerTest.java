package com.acme.retail.rag.ch16;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FaithfulnessCheckerTest {

    private final FaithfulnessChecker checker = new FaithfulnessChecker();

    private static final List<String> CONTEXT = List.of(
            "The XR-4400 blender has a 2-year manufacturer's warranty covering defects in materials and workmanship.");

    private static final String GROUNDED_SENTENCE =
            "The XR-4400 blender has a 2-year manufacturer's warranty covering defects in materials and workmanship.";
    private static final String INVENTED_SENTENCE =
            "It also includes free overnight shipping on all replacement parts.";

    @Test
    void aSentenceThatCloselyMatchesTheContextIsNotFlagged() {
        List<String> ungrounded = checker.findUngroundedSentences(GROUNDED_SENTENCE, CONTEXT);

        assertThat(ungrounded).isEmpty();
    }

    @Test
    void aConfidentlyInventedDetailWithNoSupportInTheContextIsFlagged() {
        String hallucinatedAnswer = GROUNDED_SENTENCE + " " + INVENTED_SENTENCE;

        List<String> ungrounded = checker.findUngroundedSentences(hallucinatedAnswer, CONTEXT);

        assertThat(ungrounded).containsExactly(INVENTED_SENTENCE);
    }

    @Test
    void anAnswerThatIsEntirelyInventedFlagsEverySentence() {
        String fullyHallucinatedAnswer =
                "This product ships with a lifetime guarantee. Replacement units arrive within 24 hours.";

        List<String> ungrounded = checker.findUngroundedSentences(fullyHallucinatedAnswer, CONTEXT);

        assertThat(ungrounded).hasSize(2);
    }
}
