package com.acme.retail.rag.ch01;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Stands in for the products table Volume 1's grounding already checked.
 * A real version is backed by Postgres; this chapter only needs enough to
 * prove the point, so it's an in-memory map.
 */
@Service
public class StructuredKnowledgeBase {

    private final Map<String, ProductFact> factsBySku = Map.of(
            "XR-4400", new ProductFact("XR-4400", "TrailBlend 3000 Blender", true, 8999),
            "HP-2210", new ProductFact("HP-2210", "Acme Wireless Headphones", false, 12499)
    );

    public Optional<ProductFact> findBySku(String sku) {
        return Optional.ofNullable(factsBySku.get(sku));
    }
}
