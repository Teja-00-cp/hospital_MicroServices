package com.hospital.hms.ai;

public record RagSource(
        Long recordId,
        Integer chunkNumber,
        String diagnosis,
        Double similarityScore,
        String text
) {
}
