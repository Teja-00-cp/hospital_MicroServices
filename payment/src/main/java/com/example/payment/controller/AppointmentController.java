package com.example.payment.controller;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.payment.Model.Appointment;
import com.example.payment.Service.AppointmentService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


@RestController
@RequestMapping("/appointment")
@EnableDiscoveryClient
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;
	
	@PostMapping("/addAppoint")
	public void scheduleAppointment(@RequestBody Appointment appointment) {
		System.out.println(appointment.toString());
		appointmentService.scheduleAppointment(appointment);
	}
	@GetMapping("/get/{appointmentId}")
	public Appointment getAppointmentDetails(@PathVariable long  appointmentId){
		System.out.println(appointmentService.getAppointmentDetails(appointmentId).toString());
		return appointmentService.getAppointmentDetails(appointmentId);
	}
	@DeleteMapping("/delete/{appointmentId}")
	public void cancelAppointment(@PathVariable long appointmentId){
		appointmentService.cancelAppointment(appointmentId);
	}
	@PutMapping("/update/{appointmentId}")
	public void updateAppointment(@PathVariable long appointmentId){
		appointmentService.updateAppointment(appointmentId);
	}
	@GetMapping("/appt/all")
	public List<Object[]> allDetails(){
		return appointmentService.allDetails();
	}
	@GetMapping("/giveallappoint")
	public String allDetailsApp(){
		return "Return allapp";
	}
	@GetMapping("/appt/time/{id}")
	public Iterable<String> getTime(@PathVariable long id){
		return appointmentService.getBytime(id);
	}
	@GetMapping("/appt/time/{doctorId}/{appointmentDate}")
	@CircuitBreaker(name = "welcomeOrderServiceCircuit", fallbackMethod = "localFallback")
	 public List<String> getBookedTimeSlots(@PathVariable long doctorId, @PathVariable String appointmentDate) {
        return appointmentService.getBookedTimeSlots(doctorId, appointmentDate);
    }
	
	// @GetMapping("/appt/doctorname/{doctorName}/{appointmentDate}")
	//  public List<String> getBookedTimeSlotsByName(@PathVariable String doctorName, @PathVariable String appointmentDate) {
    //     return appointmentService.getBookedTimeSlotsByName(doctorName, appointmentDate);
    // }
	@GetMapping("/appt/details/{userName}/{appointmentDate}")
	public Iterable<Object[]> getdoctorappbyToday(@PathVariable String userName,@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate) {
		System.out.println(userName+" "+appointmentDate);
		return appointmentService.getdoctorappbyToday(userName,appointmentDate);
		
	}
	@GetMapping("/iamapp")
	public String getTime(){
		return "I am from appointment service";
	}

	// @GetMapping("/appt/details/{userName}")
	// 	public Iterable<Object[]> getdoctorappbyToday(@PathVariable String userName, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
	// 		System.out.println(userName + " " + date);
	// 		return appointmentService.getdoctorappbyToday(userName,date);
	// 	}

	// }
}
