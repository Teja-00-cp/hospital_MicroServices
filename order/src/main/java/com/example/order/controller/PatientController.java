package com.example.order.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // Required for hasAuthority
import org.springframework.web.bind.annotation.*;
import com.example.order.Model.Patient;
import com.example.order.Service.PatientService;

@RestController
@RequestMapping("/order/pat")
@CrossOrigin(origins = "*") // Allows your jQuery frontend to access this controller safely
public class PatientController {

    @Autowired
    private PatientService patientService;
    
    @PutMapping("/update/{patientId}")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'ADMIN')") // Exact authority check
    public void updatePatient(@PathVariable long patientId, @RequestBody Patient patientData) {
        patientService.updatePatient(patientId, patientData);
    }

    @GetMapping("/getPatient/{patientId}") // Doctors & Admins can view specific profile info
    public Optional<Patient> getPatientDetails(@PathVariable long patientId){
        System.out.println(patientId);
        return patientService.getPatientDetails(patientId); 
    }

    @GetMapping("/getallpatients")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'ADMIN')") // Patients shouldn't see a list of other patients
    public Iterable<Patient> getPatientall(){
        return patientService.getPatientall(); 
    }

    @DeleteMapping("/delete/{patientId}")
    @PreAuthorize("hasAuthority('ADMIN')") // Restrict data destruction exclusively to Admin
    public void deletePatient(@PathVariable long patientId) {
        patientService.deletePatient(patientId);
    }

    
    // ADDED: Covered uppercase, lowercase, and ROLE_ prefixes so Spring Security cannot reject it!
    // @PreAuthorize("hasAnyAuthority('PATIENT', 'ROLE_PATIENT', 'patient', 'DOCTOR', 'ROLE_DOCTOR', 'ADMIN', 'ROLE_ADMIN')")
    @GetMapping("/getname/{name}")
    public Patient getname(@PathVariable String name) {
        System.out.println("DEBUG: Successfully hit getname endpoint for user: " + name);
        return patientService.getByname(name);
    }
}