package com.acme.retail.rag.ch08;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Turns retrieved chunks and a question into the actual prompt sent to the
 * model -- the "augment" step of retrieve-augment-generate. Built on
 * Spring AI's real {@code PromptTemplate} (from {@code spring-ai-model})
 * rather than raw string concatenation -- the same class Spring AI's own
 * retrieval-augmentation advisors use internally. The instruction to
 * answer only from the provided context, and to cite sources, is stated
 * explicitly here rather than hoped for implicitly; Chapter 16 revisits
 * why that instruction alone isn't a complete grounding guarantee.
 */
@Service
public class AugmentedPromptBuilder {

    private static final String TEMPLATE = """
            Answer the question using ONLY the context below. \
            If the answer is not contained in the context, say you don't know.

            Context:
            {context}

            Question: {question}
            """;

    public String build(String question, List<Document> retrievedChunks) {
        String contextBlock = retrievedChunks.stream()
                .map(chunk -> "[Source: " + chunk.getMetadata().getOrDefault("source", chunk.getId()) + "] "
                        + chunk.getText())
                .collect(Collectors.joining("\n"));

        PromptTemplate promptTemplate = new PromptTemplate(TEMPLATE);
        return promptTemplate.render(Map.of("context", contextBlock, "question", question));
    }
}
