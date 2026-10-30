package com.acme.retail.rag.ch13;

/**
 * One named, independently-testable unit of work. A stage's only contract
 * is "read what you need from the context, write what you produce back
 * into it" -- it has no reference to any other stage, and no idea what
 * position it occupies in the pipeline.
 */
public interface PipelineStage {

    PipelineContext process(PipelineContext context);
}
