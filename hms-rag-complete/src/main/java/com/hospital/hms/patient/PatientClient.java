package com.hospital.hms.patient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "WELCOME-ORDER-SERVICE"
)
public interface PatientClient {

    @GetMapping("/order/pat/getPatient/{patientId}")
    PatientDto getPatientById(
        @PathVariable("patientId") Long patientId
    );
}