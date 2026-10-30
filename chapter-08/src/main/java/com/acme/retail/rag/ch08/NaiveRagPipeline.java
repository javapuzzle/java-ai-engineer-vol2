package com.acme.retail.rag.ch08;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The whole point of this chapter: retrieve -&gt; augment -&gt; generate, wired
 * together end to end. This class stops right before generation -- handing
 * back the finished, augmented prompt -- because the actual model call is
 * Spring AI's {@code ChatClient}'s job, already proven in Volume 1, not
 * something this chapter needs to re-test.
 */
@Service
public class NaiveRagPipeline {

    private final ChunkVectorStore vectorStore;
    private final AugmentedPromptBuilder promptBuilder;
    private final int topK;

    public NaiveRagPipeline(ChunkVectorStore vectorStore, AugmentedPromptBuilder promptBuilder, int topK) {
        this.vectorStore = vectorStore;
        this.promptBuilder = promptBuilder;
        this.topK = topK;
    }

    public String buildAugmentedPrompt(String question) {
        List<Document> retrievedChunks = vectorStore.retrieveTopK(question, topK);

        return promptBuilder.build(question, retrievedChunks);
    }
}
