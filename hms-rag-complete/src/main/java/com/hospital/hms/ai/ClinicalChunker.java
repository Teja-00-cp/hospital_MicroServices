package com.hospital.hms.ai;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClinicalChunker {

    private static final int MAX_CHARS = 1200;
    private static final int OVERLAP_CHARS = 150;

    public List<String> chunk(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String normalized = text.trim().replace("\r\n", "\n");
        if (normalized.length() <= MAX_CHARS) {
            return List.of(normalized);
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < normalized.length()) {
            int end = Math.min(start + MAX_CHARS, normalized.length());

            if (end < normalized.length()) {
                int paragraphBreak = normalized.lastIndexOf("\n\n", end);
                int sentenceBreak = normalized.lastIndexOf(". ", end);
                int candidate = Math.max(paragraphBreak, sentenceBreak);
                if (candidate > start + (MAX_CHARS / 2)) {
                    end = candidate + 1;
                }
            }

            String chunk = normalized.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            if (end >= normalized.length()) {
                break;
            }

            int nextStart = Math.max(end - OVERLAP_CHARS, start + 1);
            start = nextStart;
        }

        return chunks;
    }
}
