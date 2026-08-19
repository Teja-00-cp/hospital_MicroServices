package com.example.bill.DTO;

import java.io.Serializable;

public class PaymentSuccessEvent implements Serializable {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String patientName;
    private String patientId;
    private String doctorId;
    private String appointmentDate;
    private String timeSlot;
    private double totalAmount;
    private long timestamp;

    public PaymentSuccessEvent() {}

    public PaymentSuccessEvent(String razorpayOrderId, String razorpayPaymentId, String patientName, 
                               String patientId, String doctorId, String appointmentDate, 
                               String timeSlot, double totalAmount) {
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.patientName = patientName;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.timeSlot = timeSlot;
        this.totalAmount = totalAmount;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}