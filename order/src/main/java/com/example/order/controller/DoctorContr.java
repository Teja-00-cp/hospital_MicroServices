package com.example.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // Required for hasAuthority
import org.springframework.web.bind.annotation.*;
import com.example.order.Model.Doctor;
import com.example.order.Service.DoctorService;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*") // Allows your jQuery frontend to access this controller safely
public class DoctorContr {

    @Autowired
    private DoctorService doctorService;
    
    @GetMapping("/getCompletedoctor")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')") // Patients can look up working doctors to find them
    public Iterable<Doctor> getCompletedoctor(){
        return doctorService.getCompletedoctor();
    }

    @GetMapping("/getalldoc")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'ROLE_PATIENT', 'patient', 'ADMIN', 'ROLE_ADMIN', 'DOCTOR', 'ROLE_DOCTOR')")
    public Iterable<Doctor> getall(){
        return doctorService.getallDoc();
    }

    @PutMapping("/updateDoc/{doctorId}")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'ADMIN')") // Doctors update themselves, or Admins manage them
    public void updatePatient(@PathVariable long doctorId, @RequestBody Doctor patientData) {
        doctorService.updateDoctor(doctorId, patientData);
    }

    @GetMapping("/get/{id}")
    // @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public Doctor getallbyId(@PathVariable long id){
        return doctorService.getallbyId(id);
    }

    @GetMapping("/getname/mm") // Fixed duplicate '/order' declaration in your original code snippet
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public String getallOrdername(){
        return "I am inside Order sser";
    }

    @GetMapping("/getname/{name}")
    // @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN','ROLE_PATIENT','ROLE_DOCTOR','ROLE_ADMIN')") // Patients, Doctors, and Admins can search by name
    public Doctor getbyName(@PathVariable String name){
        return doctorService.getbyName(name);
    }

    @DeleteMapping("/doc/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // ONLY Admins can fire/delete a Doctor record
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "Doctor with ID " + id + " has been deleted.";
    }
}