package com.hospital.hms.ehr;

import java.time.LocalDateTime;

public record MedicalRecordResponse(

        Long id,
        Long patientId,
        String diagnosis,
        String doctorNotes,
        LocalDateTime createdAt

) {

    public static MedicalRecordResponse from(MedicalRecord record) {

        return new MedicalRecordResponse(

                record.getId(),

                record.getPatientId(),
                
                record.getDiagnosis(),

                record.getDoctorNotes(),

                record.getCreatedAt()
        );
    }
}