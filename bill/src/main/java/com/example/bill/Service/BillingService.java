package com.example.bill.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bill.Model.Bill;
import com.example.bill.Model.Bill.PaymentStatus;
import com.example.bill.Repository.BillRepository;
import com.example.bill.controller.BillingController.BillData;
import com.example.bill.feign.WelcomrFeign;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import moc.tem.model.Patient;

import java.time.LocalDate;

@Service
public class BillingService {

    private final BillRepository billRepository;
    private final WelcomrFeign welcomrFeign;

    // Standardized Constructor Injection
    @Autowired
    public BillingService(BillRepository billRepository, WelcomrFeign welcomrFeign) {
        this.billRepository = billRepository;
        this.welcomrFeign = welcomrFeign;
    }

    public Iterable<Bill> generateBill(String patientName) {
        return billRepository.findByPatientId(welcomrFeign.getPatientId(patientName).getPatientId());
    }

    @CircuitBreaker(name = "failtra", fallbackMethod = "processPaymentFallback")
    public String processPaymentafter(BillData data) {
        // 1. Fetch Patient via Feign
        Patient feignpatient = welcomrFeign.getPatientId(data.getPatientName());
        System.out.println("Patient ID: " + feignpatient.getPatientId());
        
        // 2. Process Payment (Passing the ID so we don't call Feign twice)
        String status = processPayment(data, feignpatient.getPatientId());
        System.out.println(status + " | I Am Executing: ---------------------------------------");
        
        // 3. Trigger the Circuit Breaker intentionally if it fails
        if ("fail".equals(status)) {
            throw new RuntimeException("Payment processing failed intentionally");
        }

        // 4. Save to DB on Success
        Bill bill = new Bill();
        bill.setBillDate(LocalDate.now());
        bill.setPatientId(feignpatient.getPatientId());
        bill.setTotalAmount(data.getTotalAmount());
        bill.setPaymentStatus(PaymentStatus.PAID);
        billRepository.save(bill);
        
        return status;
    }

    // --- FALLBACK METHOD ---
    public String processPaymentFallback(BillData data, Throwable t) {
        // Log the exact reason the fallback was triggered (Exception or OPEN state)
        System.err.println("🛡️ Fallback Activated! Reason: " + t.getMessage());
        
        // We DO NOT call welcomrFeign here! If the circuit is OPEN, 
        // making another remote call is dangerous.
        
        Bill bill = new Bill();
        bill.setBillDate(LocalDate.now());
        bill.setTotalAmount(data.getTotalAmount());
        bill.setPaymentStatus(PaymentStatus.UNPAID); 
        
        // Note: Because Feign might be down, we cannot safely fetch the PatientId here.
        // If you must save failed bills to the database, you should include the 
        // PatientId directly inside your BillData DTO from the controller.
        
        // billRepository.save(bill); 

        // return "fail-fallback";
        if (t instanceof CallNotPermittedException) {
        // The circuit is OPEN. It has failed multiple times. System is down.
        return "fail-fallback"; 
    } else {
        // The circuit is still CLOSED. This was just a single transaction failure.
        return "fail"; 
    }
    }

    public Bill getBillDetails(Long billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + billId));
    }

    public String processPayment(BillData data, Long patientId) {
        boolean bol = welcomrFeign.getPatientDetails(patientId).isPresent();
        System.out.println("Patient exists in Feign: " + bol);
        // bol=false;
        // TEST LOGIC: 70% chance to fail, 30% chance to succeed.
        // This will force the circuit to open, but eventually allow enough successes 
        // through during the HALF-OPEN state to close the circuit again.
        // boolean isIntentionalFailure = Math.random() < 0.7; 
        
        return bol ? "success" : "fail";
    }
}