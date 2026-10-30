package com.acme.retail.rag.ch06;

import org.springframework.stereotype.Service;

/**
 * Normalizes raw text pulled out of a real-world document -- PDFs, Word
 * exports, wiki pages -- before anything downstream ever sees it. Real
 * extraction tools routinely leave behind control characters, inconsistent
 * whitespace, and stray artifacts that mean nothing to a reader but poison
 * chunking and embedding if they pass through untouched.
 */
@Service
public class DocumentCleaner {

    public String clean(String rawText) {
        String withoutControlCharacters = rawText.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
        String withCollapsedWhitespace = withoutControlCharacters.replaceAll("\\s+", " ");

        return withCollapsedWhitespace.trim();
    }
}
