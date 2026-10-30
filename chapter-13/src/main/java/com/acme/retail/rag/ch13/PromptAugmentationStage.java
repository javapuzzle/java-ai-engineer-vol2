package com.acme.retail.rag.ch13;

import java.util.stream.Collectors;

/**
 * Builds the final prompt from whatever ended up in {@code chunksForPrompt}
 * -- the same augmentation idea from Chapter 8, now written as a stage
 * that doesn't know or care whether a grading stage ran before it.
 */
public class PromptAugmentationStage implements PipelineStage {

    @Override
    public PipelineContext process(PipelineContext context) {
        String contextBlock = context.getChunksForPrompt().stream().collect(Collectors.joining("\n"));

        String prompt = """
                Answer the question using ONLY the context below. \
                If the answer is not contained in the context, say you don't know.

                Context:
                %s

                Question: %s
                """.formatted(contextBlock, context.getOriginalQuestion());

        context.setFinalPrompt(prompt);
        return context;
    }
}
