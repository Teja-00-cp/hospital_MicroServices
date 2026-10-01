package com.hospital.hms.ehr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord>
        findByPatientIdOrderByCreatedAtDesc(
                Long patientId
        );
}