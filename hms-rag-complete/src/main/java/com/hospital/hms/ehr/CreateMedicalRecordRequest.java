package com.hospital.hms.ehr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMedicalRecordRequest(
        @NotNull Long patientId,
        @NotBlank String diagnosis,
        @NotBlank String doctorNotes
) {
}
