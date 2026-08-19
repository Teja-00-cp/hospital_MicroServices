package com.example.bill.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.bill.DTO.BillData;
import com.example.bill.DTO.PaymentRequest;
import com.example.bill.Model.Bill;
import com.example.bill.Service.ABllingService;
import com.example.bill.Service.BillingService;

@RestController
@RequestMapping("/bill/api/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;
    
    @Autowired
    private ABllingService aBllingService;

    @GetMapping("/generate/{name}")
    public Iterable<Bill> generateBill(@PathVariable String name) {
        return billingService.generateBill(name);
    }

    @GetMapping("/{billId}")
    public Bill getBillDetails(@PathVariable Long billId) {
        return billingService.getBillDetails(billId);
    }

    @GetMapping("/getanybill")
    public String getanybill() {
        return "Any Bill";
    }

    @GetMapping("/loadtest")
    public String loadTest() {
        try {
            // This pauses the current thread for exactly 5 seconds
            TimeUnit.SECONDS.sleep(5); 
            // Alternative: Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Load test completed successfully -- Bill Service";
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody BillData data) throws Exception {
        // Now expecting a Map from the service, which serializes perfectly into JSON
        return ResponseEntity.ok(billingService.createOrder(data));
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentRequest request) throws Exception {

        System.out.println("OrderId = " + request.getRazorpayOrderId());
        System.out.println("PaymentId = " + request.getRazorpayPaymentId());
        System.out.println("Signature = " + request.getRazorpaySignature());
        System.out.println("Patient = " + request.getPatientName());
        System.out.println("Amount = " + request.getTotalAmount());

        BillData billData = new BillData();
        billData.setPatientName(request.getPatientName());
        billData.setTotalAmount((long)request.getTotalAmount());

        return ResponseEntity.ok(
                billingService.verifyPayment(
                        request.getRazorpayOrderId(),
                        request.getRazorpayPaymentId(),
                        request.getRazorpaySignature(),
                        billData
                )
        );
    }

    @PostMapping("/asynccreate-order")
    public ResponseEntity<Map<String, Object>> createOrderasync(@RequestBody BillData data) throws Exception {
        return ResponseEntity.ok(billingService.createOrder(data));
    }

    /**
     * ASYNCHRONOUS PAYMENT VERIFICATION ENDPOINT
     * Verifies the Razorpay signature and hands off the booking confirmation to Kafka asynchronously.
     */
    @PostMapping("/asycverify-payment")
    public ResponseEntity<String> verifyPaymentaync(@RequestBody PaymentRequest request) {
    try {
        // Convert PaymentRequest object to Map<String, Object> expected by ABllingService
        Map<String, Object> payload = new HashMap<>();
        payload.put("razorpayOrderId", request.getRazorpayOrderId());
        payload.put("razorpayPaymentId", request.getRazorpayPaymentId());
        payload.put("razorpaySignature", request.getRazorpaySignature());
        payload.put("patientName", request.getPatientName());
        payload.put("patientId", request.getPatientId());
        payload.put("doctorId", request.getDoctorId());
        payload.put("appointmentDate", request.getAppointmentDate());
        payload.put("timeSlot", request.getTimeSlot());
        payload.put("totalAmount", request.getTotalAmount());

        // Call the service with the constructed Map
        String status = aBllingService.verifyPayment(payload);

        if ("Payment Success".equalsIgnoreCase(status)) {
            return ResponseEntity.ok("PAYMENT SUCCESS");
        } else {
            return ResponseEntity.badRequest().body(status);
        }
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Error verifying payment: " + e.getMessage());
    }
}
}