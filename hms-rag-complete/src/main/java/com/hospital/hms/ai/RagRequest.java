package com.hospital.hms.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RagRequest(
        @NotNull Long patientId,
        @NotBlank String question
) {
}
