package com.acme.retail.rag.ch13;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Chapter 12's Corrective RAG idea, recast as a pipeline stage: narrows
 * whatever the previous stage placed in {@code chunksForPrompt} down to
 * chunks that share a specific product model with the question, when the
 * question names one. This stage doesn't know or care that
 * {@code RetrievalStage} ran before it -- it only reads and rewrites
 * {@code chunksForPrompt}.
 */
public class RelevanceGradingStage implements PipelineStage {

    private static final Pattern PRODUCT_MODEL_PATTERN = Pattern.compile("[A-Za-z]+\\d{2,5}");

    @Override
    public PipelineContext process(PipelineContext context) {
        Set<String> questionModels = extractModels(context.getOriginalQuestion());

        if (questionModels.isEmpty()) {
            return context;
        }

        List<String> gradedChunks = context.getChunksForPrompt().stream()
                .filter(chunk -> extractModels(chunk).stream().anyMatch(questionModels::contains))
                .toList();

        context.setChunksForPrompt(gradedChunks);
        return context;
    }

    private Set<String> extractModels(String text) {
        Matcher matcher = PRODUCT_MODEL_PATTERN.matcher(text);
        Set<String> models = new HashSet<>();
        while (matcher.find()) {
            models.add(matcher.group().toLowerCase(Locale.ROOT));
        }
        return models;
    }
}
