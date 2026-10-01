package com.hospital.hms.ehr;

import com.hospital.hms.patient.PatientClient;
import com.hospital.hms.patient.PatientDto;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PatientClient patientClient;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            ApplicationEventPublisher eventPublisher,
            PatientClient patientClient) {

        this.medicalRecordRepository = medicalRecordRepository;
        this.eventPublisher = eventPublisher;
        this.patientClient = patientClient;
    }

    @Transactional
    public MedicalRecordResponse create(
            CreateMedicalRecordRequest request) {

        // Validate/fetch patient from User/Patient Service
        PatientDto patient =
                patientClient.getPatientById(
                        request.patientId()
                );

        MedicalRecord record =
                new MedicalRecord(
                        patient.patientId(),
                        request.doctorNotes(),
                        request.diagnosis()
                );

        MedicalRecord saved =
                medicalRecordRepository.save(record);

        eventPublisher.publishEvent(
                new MedicalRecordCreatedEvent(
                        saved.getId(),
                        saved.getPatientId(),
                        saved.getDiagnosis(),
                        saved.getDoctorNotes()
                )
        );

        return MedicalRecordResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> findByPatient(
            Long patientId) {

        // Validate patient exists in User/Patient Service
        patientClient.getPatientById(patientId);

        return medicalRecordRepository
        .findByPatientIdOrderByCreatedAtDesc(patientId)
        .stream()
        .map(MedicalRecordResponse::from)
        .toList();
    }
}