package com.example.bill.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.bill.DTO.BillData;
import com.example.bill.Model.Bill;
import com.example.bill.Model.Bill.PaymentStatus;
import com.example.bill.Repository.BillRepository;
import com.example.bill.feign.WelcomrFeign;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import moc.tem.model.Patient;

@Service
public class BillingService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private WelcomrFeign welcomrFeign;

    private RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String key;

    @Value("${razorpay.key.secret}")
    private String secret;

    @Autowired
    public BillingService(BillRepository billRepository,
                          WelcomrFeign welcomrFeign,
                          @Value("${razorpay.key.id}") String key,
                          @Value("${razorpay.key.secret}") String secret)
            throws Exception {

        this.billRepository = billRepository;
        this.welcomrFeign = welcomrFeign;
        this.key = key;
        this.secret = secret;
        this.razorpayClient = new RazorpayClient(key, secret);
    }

    public Iterable<Bill> generateBill(String patientName) {
        Patient patient = welcomrFeign.getPatientId(patientName);
        return billRepository.findByPatientId(patient.getPatientId());
    }

    public Bill getBillDetails(Long billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    /**
     * Create Razorpay Order
     */
    public Map<String, Object> createOrder(BillData data) throws Exception {

        JSONObject options = new JSONObject();

        // Convert double/float amount to paise as an integer
        options.put("amount", (int) (data.getTotalAmount() * 100));
        options.put("currency", "INR");
        options.put("receipt", UUID.randomUUID().toString());

        Order order = razorpayClient.orders.create(options);

        // Map the properties manually into a standard Java Map
        // This solves the Spring Boot serialization issue
        Map<String, Object> response = new HashMap<>();
        response.put("id", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));
        response.put("receipt", order.get("receipt"));
        response.put("status", order.get("status"));

        return response;
    }

    /**
     * Verify payment and save bill
     */
    @CircuitBreaker(name = "failtra", fallbackMethod = "processPaymentFallback")
    public String verifyPayment(String razorpayOrderId,
                                String razorpayPaymentId,
                                String razorpaySignature,
                                BillData data) throws Exception {

        // Prevent crashes if the frontend sends null or missing values
        if (razorpayOrderId == null || razorpaySignature == null) {
            return "Invalid Payment Data: Missing Signature or Order ID";
        }

        JSONObject json = new JSONObject();
        json.put("razorpay_order_id", razorpayOrderId);
        json.put("razorpay_payment_id", razorpayPaymentId);
        json.put("razorpay_signature", razorpaySignature);

        boolean valid = Utils.verifyPaymentSignature(json, secret);

        if (!valid) {
            return "Invalid Signature";
        }

        Patient patient = welcomrFeign.getPatientId(data.getPatientName());
        Optional<Patient> p = welcomrFeign.getPatientDetails(patient.getPatientId());

        if (p.isEmpty()) {
            return "Patient Not Found";
        }

        Bill bill = new Bill();
        bill.setPatientId(patient.getPatientId());
        bill.setBillDate(LocalDate.now());
        bill.setTotalAmount(data.getTotalAmount());
        bill.setPaymentStatus(PaymentStatus.PAID);
        billRepository.save(bill);

        return "Payment Success";
    }

    /**
     * Circuit Breaker Fallback
     */
    public String processPaymentFallback(String razorpayOrderId,
                                         String razorpayPaymentId,
                                         String razorpaySignature,
                                         BillData data,
                                         Throwable t) {

        Bill bill = new Bill();
        bill.setBillDate(LocalDate.now());
        bill.setTotalAmount(data.getTotalAmount());
        bill.setPaymentStatus(PaymentStatus.UNPAID);

        if (t instanceof CallNotPermittedException) {
            return "Payment Service Down";
        }
        
        t.printStackTrace();
        return "Payment Failed";
    }
}