package com.acme.retail.rag.ch13;

import org.springframework.ai.document.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A deliberately loose first pass: any document sharing at least one
 * meaningful word with the question is retrieved. Realistic naive
 * retrieval often over-retrieves exactly like this -- broad recall, weak
 * precision -- which is precisely what makes the grading stage that
 * follows it meaningful rather than redundant. Operates on real Spring AI
 * {@link Document} instances (from {@code spring-ai-commons}).
 */
public class RetrievalStage implements PipelineStage {

    private static final Set<String> STOPWORDS = Set.of(
            "i", "a", "the", "is", "it", "to", "for", "of", "can", "do", "does", "this", "that",
            "my", "me", "in", "what"
    );

    private final List<Document> corpus;

    public RetrievalStage(List<Document> corpus) {
        this.corpus = corpus;
    }

    @Override
    public PipelineContext process(PipelineContext context) {
        Set<String> questionWords = tokenize(context.getOriginalQuestion());

        List<String> matches = corpus.stream()
                .filter(doc -> tokenize(doc.getText()).stream().anyMatch(questionWords::contains))
                .map(Document::getText)
                .toList();

        context.setRetrievedChunks(matches);
        context.setChunksForPrompt(matches);
        return context;
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(word -> !word.isBlank())
                .filter(word -> !STOPWORDS.contains(word))
                .collect(Collectors.toSet());
    }
}
