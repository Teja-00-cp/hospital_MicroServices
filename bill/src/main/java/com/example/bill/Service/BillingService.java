package com.example.bill.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.bill.Model.Bill;
import com.example.bill.Model.Bill.PaymentStatus;
import com.example.bill.Repository.BillRepository;
import com.example.bill.controller.BillingController.BillData;
import com.example.bill.feign.WelcomrFeign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import moc.tem.model.Patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Service
public class BillingService {

    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private WelcomrFeign welcomrFeign;

    public Iterable<Bill> generateBill(Long patientId) {
        System.out.println("Generating bill for patient ID: " + billRepository.findByPatientId(patientId));

    	return billRepository.findByPatientId(patientId);

    }
    @CircuitBreaker(name = "failtra", fallbackMethod = "processPaymentFallback")
    public String processPaymentafter(BillData data) {
        Patient feignpatient=welcomrFeign.getPatientDetails(data.getPatientId()).orElse(null);
    	String status=processPayment(data);
        System.out.println(status+"I Am Executing: ---------------------------------------");
    	Bill bill=new Bill();
    	bill.setBillDate(LocalDate.now());
    	bill.setPatientId(data.getPatientId());
    	bill.setTotalAmount(data.getTotalAmount());
    	bill.setPaymentStatus(status=="fail"?PaymentStatus.UNPAID:PaymentStatus.PAID);
        if("fail".equals(status)) {
            throw new RuntimeException("Payment processing failed");
        }
    	billRepository.save(bill);
    	
        return status;
    }
    public String processPaymentFallback(BillData data, Throwable t) {
        // Log the exception for diagnostics
        System.err.println("Circuit Breaker triggered or call failed: " + t.getMessage());

        // 1. Handle the payment as 'fail' but ensure the bill is recorded.
        String status = "fail"; 
        
        // 2. Record the failed transaction to the database
        Bill bill = new Bill();
        bill.setBillDate(LocalDate.now());
        bill.setPatientId(data.getPatientId());
        bill.setTotalAmount(data.getTotalAmount());
        bill.setPaymentStatus(PaymentStatus.UNPAID); // Always UNPAID on fallback
        
        // Optional: Save the error message in the Bill entity if you have a field for it.
        // bill.setFailureReason("Patient Service Unavailable: " + t.getClass().getSimpleName());
        
        // billRepository.save(bill);
        
        // 3. Return the failure status
        return status;
    }

    public Bill getBillDetails(Long billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + billId));
    }

    public String processPayment(BillData data) {
    	boolean bol=welcomrFeign.getPatientDetails(data.getPatientId()).isPresent();
    	System.out.println(bol);
    	
        return System.currentTimeMillis()%2==0 && bol?"success":"fail";
    }
}