package com.example.payment.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.payment.Model.Appointment;
import com.example.payment.Service.AppointmentService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping("/appointment")
@EnableDiscoveryClient
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    
    // --- REDIS LOCKING ENDPOINTS START ---

    @PostMapping("/block")
    public ResponseEntity<?> blockSlot(@RequestBody Map<String, String> request) {
        long doctorId = Long.parseLong(request.get("doctorId"));
        String date = request.get("date");
        String timeSlot = request.get("timeSlot");
        String patientId = request.get("patientId");

        boolean success = appointmentService.blockSlot(doctorId, date, timeSlot, patientId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Time slot secured for 10 minutes."));
        } else {
            return ResponseEntity.status(409).body(Map.of("error", "Slot is currently blocked by another patient."));
        }
    }

    @PostMapping("/revert")
    public ResponseEntity<?> revertSlot(@RequestBody Map<String, String> request) {
        long doctorId = Long.parseLong(request.get("doctorId"));
        String date = request.get("date");
        String timeSlot = request.get("timeSlot");
        String patientId = request.get("patientId");

        boolean success = appointmentService.revertSlot(doctorId, date, timeSlot, patientId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Time slot released."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot release this slot."));
        }
    }

    @PostMapping("/confirmLock")
    public ResponseEntity<?> confirmScheduledAppointment(@RequestParam String patientId, @RequestBody Appointment appointment) {
        boolean success = appointmentService.scheduleAppointmentWithLock(appointment, patientId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Appointment Confirmed Successfully!"));
        } else {
            return ResponseEntity.status(400).body(Map.of("error", "Your session expired or lock invalid."));
        }
    }

    // --- REDIS LOCKING ENDPOINTS END ---

    @PostMapping("/addAppoint")
    public void scheduleAppointment(@RequestBody Appointment appointment) {
        System.out.println(appointment.toString());
        appointmentService.scheduleAppointment(appointment);
    }
    
    @GetMapping("/get/{appointmentId}")
    public Appointment getAppointmentDetails(@PathVariable long appointmentId){
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
    
    @GetMapping("/loadtest")
    public String loadTest() {
        try {
            TimeUnit.SECONDS.sleep(5); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Load test completed successfully -- Order Service";
    }

    @GetMapping("/appt/details/{userName}/{appointmentDate}")
    public Iterable<Object[]> getdoctorappbyToday(@PathVariable String userName, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate) {
        return appointmentService.getdoctorappbyToday(userName, appointmentDate);
    }
    
    @GetMapping("/iamapp")
    public String getTime(){
        return "I am from appointment service";
    }
}