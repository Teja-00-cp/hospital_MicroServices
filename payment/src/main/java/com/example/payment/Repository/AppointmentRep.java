package com.example.payment.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.payment.Model.Appointment;
import com.example.payment.Model.Appointment.Status;

// @Repository
// public interface AppointmentRep extends JpaRepository<Appointment, Long>{

// 	  @Query(value = """
// 	            SELECT a.appointment_id, a.appointment_date, a.time_slot, a.status, 
// 	                   p.patient_id, p.name AS patient_name,
// 	                   d.doctor_id, d.name AS doctor_name, d.specialization
// 	            FROM appointment a
// 	            LEFT JOIN patient p ON a.patient_id = p.patient_id
// 	            LEFT JOIN doctor d ON a.doctor_id = d.doctor_id
// 	            """, nativeQuery = true)
// 	    List<Object[]> findAppointmentsWithPatientAndDoctorDetails();
// 	  @Query(value = """
//     SELECT a.appointment_id, a.appointment_date, a.time_slot, a.status, 
//            p.patient_id, p.name AS patient_name,
//            d.doctor_id, d.name AS doctor_name, d.specialization
//     FROM appointment a
//     LEFT JOIN patient p ON a.patient_id = p.patient_id
//     LEFT JOIN doctor d ON a.doctor_id = d.doctor_id
//     WHERE a.appointment_date = :appointmentDate AND d.doctor_id = :doctorId
//     """, nativeQuery = true)
// List<Object[]> findAppointmentsWithDetailsByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("appointmentDate") LocalDate appointmentDate);
	    
	    
// 	    @Query(value = "SELECT a.time_slot FROM appointment a WHERE a.doctor_id = :doctorId", nativeQuery = true)
// 	    Iterable<String> findTimeSlotsByDoctorId(@Param("doctorId") Long doctorId);
// //	    List<Appointment> findByDoctorDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);
// 	    List<Appointment> findByDoctorIdAndAppointmentDateAndStatus(Long doctorId, LocalDate appointmentDate, Status status);


// }
@Repository
public interface AppointmentRep extends JpaRepository<Appointment, Long> {

    @Query(value = """
    SELECT a.appointment_id, a.appointment_date, a.time_slot, a.status, 
           p.patient_id, p.name AS patient_name,
           d.doctor_id, d.name AS doctor_name, d.specialization
    FROM appointment a
    LEFT JOIN `microservice-user`.patient p ON a.patient_id = p.patient_id
    LEFT JOIN `microservice-user`.doctor d ON a.doctor_id = d.doctor_id
    """, nativeQuery = true)
List<Object[]> findAppointmentsWithPatientAndDoctorDetails();

@Query(value = """
    SELECT a.appointment_id, a.appointment_date, a.time_slot, a.status, 
           p.patient_id, p.name AS patient_name,
           d.doctor_id, d.name AS doctor_name, d.specialization
    FROM appointment a
    LEFT JOIN `microservice-user`.patient p ON a.patient_id = p.patient_id
    LEFT JOIN `microservice-user`.doctor d ON a.doctor_id = d.doctor_id
    WHERE a.appointment_date = :appointmentDate AND d.doctor_id = :doctorId
    """, nativeQuery = true)
List<Object[]> findAppointmentsWithDetailsByDoctorAndDate(
    @Param("doctorId") Long doctorId, 
    @Param("appointmentDate") LocalDate appointmentDate);
	    
	    
	    @Query(value = "SELECT a.time_slot FROM appointment a WHERE a.doctor_id = :doctorId", nativeQuery = true)
	    Iterable<String> findTimeSlotsByDoctorId(@Param("doctorId") Long doctorId);
//	    List<Appointment> findByDoctorDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);
	    List<Appointment> findByDoctorIdAndAppointmentDateAndStatus(Long doctorId, LocalDate appointmentDate, Status status);

}
