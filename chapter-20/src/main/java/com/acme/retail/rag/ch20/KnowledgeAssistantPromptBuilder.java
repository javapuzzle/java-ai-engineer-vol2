package com.acme.retail.rag.ch20;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Chapter 8's {@code AugmentedPromptBuilder}, unchanged in shape, reused
 * here as the capstone's final step -- built on Spring AI's real {@code
 * PromptTemplate} rather than raw string concatenation.
 */
@Service
public class KnowledgeAssistantPromptBuilder {

    private static final String TEMPLATE = """
            Answer the question using ONLY the context below. \
            If the answer is not contained in the context, say you don't know.

            Context:
            {context}

            Question: {question}
            """;

    public String build(String question, List<Document> relevantChunks) {
        String contextBlock = relevantChunks.stream()
                .map(chunk -> "[Source: " + chunk.getMetadata().getOrDefault("source", chunk.getId()) + "] "
                        + chunk.getText())
                .collect(Collectors.joining("\n"));

        PromptTemplate promptTemplate = new PromptTemplate(TEMPLATE);
        return promptTemplate.render(Map.of("context", contextBlock, "question", question));
    }
}
