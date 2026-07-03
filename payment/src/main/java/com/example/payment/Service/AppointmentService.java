package com.example.payment.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.payment.Model.Appointment;
import com.example.payment.Model.Appointment.Status;
import com.example.payment.Repository.AppointmentRep;
import com.example.payment.client.WelcomrFeign;
import com.example.payment.redis.RedisService; // Make sure this import matches your RedisService package

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import moc.tem.model.Doctor;

@Service
public class AppointmentService {

    private static final String WELCOME_SERVICE = "welcomeOrderServiceCircuit"; 
    
    @Autowired
    private AppointmentRep appointmentRep;
    
    @Autowired
    private WelcomrFeign feign;

    @Autowired
    private RedisService redisService; // Injecting your Redis Service

    // --- REDIS LOCKING LOGIC START ---

    private String generateRedisKey(long doctorId, String date, String timeSlot) {
        return String.format("APPT:DOC:%d:DATE:%s:TIME:%s", doctorId, date, timeSlot);
    }

    public boolean blockSlot(long doctorId, String date, String timeSlot, String patientId) {
        String redisKey = generateRedisKey(doctorId, date, timeSlot);
        String existingHolder = redisService.get(redisKey);
        
        if (existingHolder != null && !existingHolder.equals(patientId)) {
            return false; // Slot is already blocked by someone else
        }
        
        // Block for 10 minutes using your RedisService method
        redisService.saveWithExpiration(redisKey, patientId, 10);
        return true;
    }

    public boolean revertSlot(long doctorId, String date, String timeSlot, String patientId) {
        String redisKey = generateRedisKey(doctorId, date, timeSlot);
        String existingHolder = redisService.get(redisKey);
        
        if (patientId.equals(existingHolder)) {
            redisService.delete(redisKey);
            return true;
        }
        return false;
    }

    // Updated scheduling method that checks the Redis lock first
    public boolean scheduleAppointmentWithLock(Appointment appointment, String patientId) {
        String dateStr = appointment.getAppointmentDate().toString();
        String redisKey = generateRedisKey(appointment.getDoctorId(), dateStr, appointment.getTimeSlot());
        
        String existingHolder = redisService.get(redisKey);

        if (patientId.equals(existingHolder)) {
            // Lock is valid, save to DB
            appointment.setStatus(Status.CONFIRMED);
            appointmentRep.save(appointment);
            
            // Remove the temporary Redis lock
            redisService.delete(redisKey);
            return true;
        }
        return false; // Lock expired or belongs to someone else
    }
    // --- REDIS LOCKING LOGIC END ---

    // Original fallback schedule method
    public void scheduleAppointment(Appointment appointment) {
        appointment.setStatus(Status.CONFIRMED);
        appointmentRep.save(appointment);
    }
    
    public Appointment getAppointmentDetails(long appointmentId){
        return appointmentRep.findById(appointmentId).orElse(new Appointment());
    }
    
    public void cancelAppointment(long appointmentId){
        appointmentRep.deleteById(appointmentId);
    }
    
    public void updateAppointment(long appointmentId){
        Appointment appointment=appointmentRep.findById(appointmentId).orElse(new Appointment());
        appointment.setStatus(Status.CANCELLED);
        appointmentRep.save(appointment);
    }
    
    public List<Object[]> allDetails(){
        return appointmentRep.findAppointmentsWithPatientAndDoctorDetails();
    }
    
    public Iterable<String> getBytime(long id){
        return appointmentRep.findTimeSlotsByDoctorId(id);
    }

    @CircuitBreaker(name = WELCOME_SERVICE, fallbackMethod = "fallbackGetDoctorAppByToday")
    @Retry(name = "welcomeOrderServiceRetry")
    public Iterable<Object[]> getdoctorappbyToday(String userName, LocalDate appointmentDate){
         long doctorId = feign.getbyName(userName).getDoctorId();
        return appointmentRep.findAppointmentsWithDetailsByDoctorAndDate(doctorId,appointmentDate);
    }
    
    private static final int SLOT_INTERVAL_MINUTES = 30;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mma",Locale.US);

    @CircuitBreaker(name = WELCOME_SERVICE, fallbackMethod = "fallbackGetDoctorDetails")
    @Retry(name = "welcomeOrderServiceRetry")
    public List<String> getBookedTimeSlots(long doctorId, String appointmentDate) {
        String availability = feign.getDoctorDetails(doctorId).getAvailabilitySchedule();
        String[] parts = availability.split("-");
        LocalTime startTime = LocalTime.parse(parts[0].trim(), TIME_FORMATTER);
        LocalTime endTime = LocalTime.parse(parts[1].trim(), TIME_FORMATTER);
        
        List<String> allSlots = generateTimeSlots(startTime, endTime);
        
        LocalDate date = LocalDate.parse(appointmentDate);
        List<Appointment> bookedAppointments = appointmentRep.findByDoctorIdAndAppointmentDateAndStatus(doctorId, date, Status.CONFIRMED);
        
        List<String> bookedSlots = bookedAppointments.stream()
            .map(Appointment::getTimeSlot)
            .collect(Collectors.toList());
            
        allSlots.removeAll(bookedSlots);
        
        return allSlots;
    }

    public List<String> generateTimeSlots(LocalTime start, LocalTime end) {
        List<String> slots = new ArrayList<>();
        LocalTime currentTime = start;
        
        while (currentTime.isBefore(end)) {
            LocalTime nextSlot = currentTime.plusMinutes(SLOT_INTERVAL_MINUTES);
            String slotString = currentTime.format(TIME_FORMATTER) + "-" + nextSlot.format(TIME_FORMATTER);
            slots.add(slotString);
            currentTime = nextSlot;
        }
        
        return slots;
    }

    @CircuitBreaker(name = WELCOME_SERVICE, fallbackMethod = "fallbackGetBookedTimeSlotsByName")
    @Retry(name = "welcomeOrderServiceRetry")
    public List<String> getBookedTimeSlotsByName(String doctorName, String appointmentDate) {
        long doctorId = feign.getbyName(doctorName).getDoctorId();
        return getBookedTimeSlots(doctorId, appointmentDate);
    }
}

//	 public List<String> getBookedTimeSlots( long doctorId,  String appointmentDate) {
//			List<String> timeSlot = new ArrayList<>(Arrays.asList(
//		            "09:00 AM - 09:30 AM",
//		            "09:30 AM - 10:00 AM",
//		            "10:00 AM - 10:30 AM",
//		            "10:30 AM - 11:00 AM",
//		            "11:00 AM - 11:30 AM",
//		            "11:30 AM - 12:00 PM",
//		            "12:00 PM - 12:30 PM",
//		            "12:30 PM - 01:00 PM",
//		            "01:00 PM - 01:30 PM",
//		            "01:30 PM - 02:00 PM",
//		            "02:00 PM - 02:30 PM",
//		            "02:30 PM - 03:00 PM",
//		            "03:00 PM - 03:30 PM",
//		            "03:30 PM - 04:00 PM",
//		            "04:00 PM - 04:30 PM",
//		            "04:30 PM - 05:00 PM",
//		            "05:00 PM - 05:30 PM",
//		            "05:30 PM - 06:00 PM"
//		        ));
//	        LocalDate date = LocalDate.parse(appointmentDate);
//	        String time=docRe.findById(doctorId).get().getAvailabilitySchedule();
//	        String doctimst=time.substring(0,7),doctimen=time.substring(time.length()-6,time.length());
//	        System.out.println(doctimst +"   " +doctimen);
//	        List<Appointment> bookedAppointments = appointmentRep.findByDoctorDoctorIdAndAppointmentDateAndStatus(doctorId, date,Status.CONFIRMED);
//	        List<String> ans= bookedAppointments.stream()
//	            .map(Appointment::getTimeSlot)
//	            .collect(Collectors.toList());
//	        List<String> availableSlots =removeTime(doctimst,doctimen);
//	        System.out.println("list remove:"+availableSlots );
////	        for(int i=0;i<ans.size();i++) {
////	        	if(remans.contains(ans.get(i))) remans.remove(ans.get(i));
////	        }
//	        availableSlots.removeAll(ans);
////	        System.out.println("filtered"+remans);
////	        System.out.println("after removed"+ans);
//	        
//	        return availableSlots;
//	    }
//	 
//	 public static int parseTime(String time) {
//	        time = time.replace(" ", "");
//	        int hour = Integer.parseInt(time.substring(0, time.indexOf(':')));
//	        int minute = Integer.parseInt(time.substring(time.indexOf(':') + 1, time.indexOf('A') != -1 ? time.indexOf('A') : time.indexOf('P')));
//	        boolean isPM = time.contains("PM");
//	        if (hour == 12) hour = 0;
//	        int total = hour * 60 + minute;
//	        if (isPM) total += 12 * 60;
//	        return total;
//	    }
//
//	    public static List<String> filterTimeSlots(List<String> timeSlots, String start, String end) {
//	        int filterStart = parseTime(start);
//	        int filterEnd = parseTime(end);
//
//	        return timeSlots.stream()
//	            .filter(slot -> {
//	                String[] parts = slot.split(" - ");
//	                int slotStart = parseTime(parts[0]);
//	                int slotEnd = parseTime(parts[1]);
//	                return slotStart >= filterStart && slotEnd <= filterEnd;
//	            })
//	            .collect(Collectors.toList());
//	    }
//
//	    List<String> removeTime(String start,String end) {
//	        List<String> timeSlot = new ArrayList<>(Arrays.asList(
//	            "09:00 AM - 09:30 AM",
//	            "09:30 AM - 10:00 AM",
//	            "10:00 AM - 10:30 AM",
//	            "10:30 AM - 11:00 AM",
//	            "11:00 AM - 11:30 AM",
//	            "11:30 AM - 12:00 PM",
//	            "12:00 PM - 12:30 PM",
//	            "12:30 PM - 01:00 PM",
//	            "01:00 PM - 01:30 PM",
//	            "01:30 PM - 02:00 PM",
//	            "02:00 PM - 02:30 PM",
//	            "02:30 PM - 03:00 PM",
//	            "03:00 PM - 03:30 PM",
//	            "03:30 PM - 04:00 PM",
//	            "04:00 PM - 04:30 PM",
//	            "04:30 PM - 05:00 PM",
//	            "05:00 PM - 05:30 PM",
//	            "05:30 PM - 06:00 PM"
//	        ));
//
//	        
//
//	        List<String> filteredSlots = filterTimeSlots(timeSlot, start, end);
//
//	        System.out.println("Filtered Time Slots:");
//	        filteredSlots.forEach(System.out::println);
//	        return filteredSlots;
//	    }
//

//
////	
//}
