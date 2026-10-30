package com.acme.retail.rag.ch13;

import java.util.List;

/**
 * The whole point of this chapter: a pipeline is just a list of stages,
 * run in order. Adding, removing, or reordering a stage means editing
 * this list -- never editing a stage's own code, and never risking
 * breaking a stage that isn't being touched.
 */
public class ModularRagPipeline {

    private final List<PipelineStage> stages;

    public ModularRagPipeline(List<PipelineStage> stages) {
        this.stages = stages;
    }

    public PipelineContext run(String question) {
        PipelineContext context = new PipelineContext(question);

        for (PipelineStage stage : stages) {
            context = stage.process(context);
        }

        return context;
    }
}
