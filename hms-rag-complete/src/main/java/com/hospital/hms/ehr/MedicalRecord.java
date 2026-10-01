package com.hospital.hms.ehr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "doctor_notes", nullable = false, columnDefinition = "TEXT")
    private String doctorNotes;

    @Column(nullable = false, length = 255)
    private String diagnosis;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected MedicalRecord() {
    }

    public MedicalRecord(
            Long patientId,
            String doctorNotes,
            String diagnosis) {

        this.patientId = patientId;
        this.doctorNotes = doctorNotes;
        this.diagnosis = diagnosis;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}