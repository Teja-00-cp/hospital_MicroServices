package com.example.bill.feign;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import moc.tem.model.Doctor;
import moc.tem.model.Patient;

@FeignClient(name = "WELCOME-ORDER-SERVICE")
public interface WelcomrFeign {
	@GetMapping("/order/get/{id}")
    Doctor getDoctorDetails(@PathVariable Long id); 
	@GetMapping("/order/getname/{name}")
	public Doctor getbyName(@PathVariable String name);
	@GetMapping("/order/pat/getPatient/{patientId}")
	public Optional<Patient> getPatientDetails(@PathVariable long patientId);
	@GetMapping("/order/pat/getname/{name}")
	public Patient getPatientId(@PathVariable String name);
}
