package com.acme.retail.rag.ch17;

import java.util.List;

/**
 * One row of a golden evaluation dataset: a real question, the specific
 * facts a correct answer needs to be backed by, what retrieval actually
 * found, and what generation actually produced. Each of the three scorers
 * in this chapter reads a different combination of these four fields --
 * which is exactly why they measure different things.
 */
public record GoldenCase(
        String question,
        List<String> requiredContextFacts,
        List<String> retrievedContext,
        String generatedAnswer
) {
}
