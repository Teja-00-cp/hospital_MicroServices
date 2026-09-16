package com.example.order.Service;



import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.example.order.Model.Doctor;
import com.example.order.Repository.DocRe;


@Service
public class DoctorService {
	@Autowired
	private DocRe docRe;
	
	public void addDoc(Doctor doctor) {
			docRe.save(doctor);
	}
	public Iterable<Doctor> getallDoc(){
		return docRe.findAll();
	}
	public Doctor getallbyId(@PathVariable long id){
		return docRe.findById(id).orElse(null);
	}
	public Doctor getbyName(@PathVariable String name){
		return docRe.findByName(name);
	}
//	public Iterable<String> getTime(@PathVariable long id){
//		Iterable<Doctor> time=docRe.findAllById(id).orElse(null);
//		
//		}
	public Iterable<Doctor> getCompletedoctor() {
		// TODO Auto-generated method stub
		return docRe.findAll();
	}
    public void updateDoctor(long patientId, Doctor doctorData) {
        Doctor existingPatient = docRe.findById(patientId).orElse(null);
			System.out.println(doctorData.toString()+"  "+patientId);
		if (existingPatient != null) {
			// In your Doctor Service:
try {
    // Your schedule parsing logic here

			System.out.println(doctorData.toString());
			existingPatient.setAvailabilitySchedule(doctorData.getAvailabilitySchedule());
			existingPatient.setSpecialization(doctorData.getSpecialization());	
			existingPatient.setContactNumber(doctorData.getContactNumber());	
			docRe.save(existingPatient);} catch (DateTimeParseException | IllegalArgumentException ex) {
    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST, 
        "Invalid availability schedule format. Expected format: 04:00AM-08:00PM"
    );
}
		}
			else{
				throw new RuntimeException("Patient not found with id: " + patientId);
			}
			
    }
	public void deleteDoctor(Long id) {
		docRe.deleteById(id);
	}
}
