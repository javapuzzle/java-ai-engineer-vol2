package com.acme.retail.rag.ch12;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Grades a chunk IRRELEVANT when the question names a specific product
 * model and the chunk names a different one -- catching the exact failure
 * this chapter opens with: a warranty chunk about a similar, but wrong,
 * product line, retrieved because it's topically close, not because it
 * actually answers the question asked. If the question names no specific
 * model, this grader defers entirely to retrieval and grades RELEVANT --
 * it only overrides retrieval on this one, narrow, checkable signal.
 */
@Service
public class ProductIdentifierRelevanceGrader implements RelevanceGrader {

    private static final Pattern PRODUCT_MODEL_PATTERN = Pattern.compile("[A-Za-z]+\\d{2,5}");

    @Override
    public RelevanceGrade grade(String query, String chunkText) {
        Set<String> queryModels = extractModels(query);

        if (queryModels.isEmpty()) {
            return RelevanceGrade.RELEVANT;
        }

        Set<String> chunkModels = extractModels(chunkText);
        boolean sharesAtLeastOneModel = queryModels.stream().anyMatch(chunkModels::contains);

        return sharesAtLeastOneModel ? RelevanceGrade.RELEVANT : RelevanceGrade.IRRELEVANT;
    }

    private Set<String> extractModels(String text) {
        Matcher matcher = PRODUCT_MODEL_PATTERN.matcher(text);
        Set<String> models = new java.util.HashSet<>();
        while (matcher.find()) {
            models.add(matcher.group().toLowerCase(Locale.ROOT));
        }
        return models.stream().collect(Collectors.toSet());
    }
}
