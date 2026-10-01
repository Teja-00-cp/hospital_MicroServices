package com.hospital.hms.ehr;

public record MedicalRecordCreatedEvent(
        Long recordId,
        Long patientId,
        String diagnosis,
        String doctorNotes
) {
}
