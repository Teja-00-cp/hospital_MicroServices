package com.hospital.hms.patient;


import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PatientIndexService {

    private final PatientClient patientClient;
    private final VectorStore vectorStore;

    public PatientIndexService(
            PatientClient patientClient,
            VectorStore vectorStore) {

        this.patientClient = patientClient;
        this.vectorStore = vectorStore;
    }


    public void indexPatient(Long patientId) {

        // Call your existing User/Patient microservice
        PatientDto patient =
            patientClient.getPatientById(patientId);


        // Nothing useful for RAG if medical history is empty
        if (patient.medicalHistory() == null
                || patient.medicalHistory().isBlank()) {

            return;
        }


        Document document =
            new Document(

                "patient-medical-history-" + patient.patientId(),

                patient.medicalHistory(),

                Map.of(
                    "patientId", patient.patientId(),
                    "patientName", patient.name(),
                    "source", "user-service",
                    "documentType", "medical-history"
                )
            );


        vectorStore.add(
            List.of(document)
        );
    }
}