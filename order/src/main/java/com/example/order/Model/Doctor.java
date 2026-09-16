package com.example.order.Model;


// Removed all relationship-specific imports as they are not needed for ID-based referencing
// import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonManagedReference;
// import jakarta.persistence.CascadeType;
// import jakarta.persistence.OneToMany;
// import moc.tem.model.Appointment; // The DTO is not needed in the entity for persistence

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ElementCollection; // To store a collection of simple types (Long)

import java.util.*;
import jakarta.persistence.CollectionTable;  // To define the table for the IDs
import jakarta.persistence.JoinColumn;       // To join the ID table back to Doctor
import jakarta.validation.constraints.Pattern;

@Entity
public class Doctor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long doctorId ;
	
	@Column(unique = true)
	private String name;
	private String specialization; 
	private String contactNumber ;
	@Pattern(regexp = "^(0[1-9]|1[0-2]):[0-5][0-9](AM|PM)-(0[1-9]|1[0-2]):[0-5][0-9](AM|PM)$", 
             message = "Availability schedule must follow format 'HH:MMAM-HH:MMPM'")
	private String availabilitySchedule;

    // --- REPLACED COMPLEX JPA/DTO RELATIONSHIP WITH A LIST OF FOREIGN KEYS (IDs) ---
    
    // Stores the list of Long IDs in a separate table managed by JPA
    @ElementCollection 
    @CollectionTable(
        name = "doctor_appointment_ids", 
        joinColumns = @JoinColumn(name = "doctor_id")
    )
    @Column(name = "appointment_id")
    private List<Long> appointmentIds;

    // --- End of Microservice ID Mapping ---

	@Override
	public String toString() {
		// Cleaned up toString() to reference the ID list, not the full DTO/Entity list
		return "Doctor [doctorId=" + doctorId + ", name=" + name + ", specialization=" + specialization
				+ ", contactNumber=" + contactNumber + ", availabilitySchedule=" + availabilitySchedule
				+ ", appointmentIds=" + appointmentIds + "]";
	}

	
	public List<Long> getAppointmentIds() {
		return appointmentIds;
	}


	public void setAppointmentIds(List<Long> appointmentIds) {
		this.appointmentIds = appointmentIds;
	}
    
    // Removed the old getAppointments/setAppointments which used List<Appointment>

	public Doctor() {
		super();
	}

	
	public long getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(long doctorId) {
		this.doctorId = doctorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSpecialization() {
		return specialization;
	}
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getAvailabilitySchedule() {
		return availabilitySchedule;
	}
	public void setAvailabilitySchedule(String availabilitySchedule) {
		this.availabilitySchedule = availabilitySchedule;
	}

}