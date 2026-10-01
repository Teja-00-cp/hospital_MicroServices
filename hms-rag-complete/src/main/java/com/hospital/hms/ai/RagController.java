package com.hospital.hms.ai;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.hms.patient.PatientIndexService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final PatientIndexService patientIndexService;

    private final ClinicalRagService ragService;


    public RagController(
            PatientIndexService patientIndexService,
            ClinicalRagService ragService) {

        this.patientIndexService =
            patientIndexService;

        this.ragService =
            ragService;
    }


    @PostMapping(
        "/patients/{patientId}/index"
    )
    public ResponseEntity<String>
        indexPatient(

            @PathVariable
            Long patientId) {

        patientIndexService
            .indexPatient(patientId);

        return ResponseEntity.ok(
            "Patient medical history indexed successfully."
        );
    }


    @PostMapping("/ask")
    public ResponseEntity<RagResponse>
        ask(
            @Valid
            @RequestBody
            RagRequest request) {

        RagResponse response =
            ragService.ask(
                request.patientId(),
                request.question()
            );

        return ResponseEntity.ok(response);
    }
}