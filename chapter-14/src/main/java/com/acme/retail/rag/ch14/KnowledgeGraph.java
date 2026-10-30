package com.acme.retail.rag.ch14;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A minimal knowledge graph: a flat list of relationships, queried in
 * either direction. Real graph databases (like Neo4j) index this for
 * traversal at scale; this in-memory version is enough to prove the
 * traversal idea itself.
 */
@Service
public class KnowledgeGraph {

    private final List<Relationship> relationships;

    public KnowledgeGraph(List<Relationship> relationships) {
        this.relationships = relationships;
    }

    /** Forward hop: given a subject and predicate, what objects does it point to? */
    public List<String> findObjects(String subject, String predicate) {
        return relationships.stream()
                .filter(r -> r.subject().equals(subject) && r.predicate().equals(predicate))
                .map(Relationship::object)
                .toList();
    }

    /** Backward hop: given a predicate and object, what subjects point to it? */
    public List<String> findSubjects(String predicate, String object) {
        return relationships.stream()
                .filter(r -> r.predicate().equals(predicate) && r.object().equals(object))
                .map(Relationship::subject)
                .toList();
    }
}
