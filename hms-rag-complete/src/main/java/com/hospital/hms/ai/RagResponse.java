package com.hospital.hms.ai;

import java.util.List;

public record RagResponse(
        Long patientId,
        String question,
        String answer,
        List<RagSource> sources
) {
}
