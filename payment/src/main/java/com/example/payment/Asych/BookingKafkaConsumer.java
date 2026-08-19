package com.example.payment.Asych;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.payment.Dto.PaymentSuccessEvent;
import com.example.payment.Model.Appointment;
import com.example.payment.Repository.AppointmentRep;
@Service
public class BookingKafkaConsumer {

    @Autowired
    private AppointmentRep appointmentRepository;

    @Autowired
    private SlotLockManager slotLockManager;

    @KafkaListener(topics = "payment-events",groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentSuccess(PaymentSuccessEvent event) {
        try {
            System.out.println("Processing Payment Event for Patient ID: " + event.getPatientId());

            // Check for null values to prevent NullPointerExceptions
            if (event.getPatientId() == null || event.getDoctorId() == null) {
                System.err.println("Skipping invalid event: Patient or Doctor ID is null");
                return; // Gracefully exit without throwing an exception
            }

            Appointment appointment = new Appointment();
            appointment.setPatientId(Long.parseLong(event.getPatientId()));
            appointment.setDoctorId(Long.parseLong(event.getDoctorId()));
            appointment.setAppointmentDate(LocalDate.parse(event.getAppointmentDate()));
            appointment.setTimeSlot(event.getTimeSlot());
            // appointment.setPaymentId(event.getRazorpayPaymentId());
            appointment.setStatus(Appointment.Status.CONFIRMED);

            appointmentRepository.save(appointment);

            String lockKey = event.getDoctorId() + "_" + event.getAppointmentDate() + "_" + event.getTimeSlot();
            slotLockManager.releaseSlot(lockKey);

            System.out.println("Booking confirmed for Patient ID: " + event.getPatientId());

        } catch (Exception e) {
            // Log the error ONCE and absorb it so Kafka doesn't loop forever
            System.err.println("Failed to process appointment event: " + e.getMessage());
        }
    }
}