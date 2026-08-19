package com.example.bill.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.bill.DTO.BillData;
import com.example.bill.DTO.PaymentSuccessEvent;
import com.example.bill.Model.Bill;
import com.example.bill.Model.Bill.PaymentStatus;
import com.example.bill.Repository.BillRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@Service
public class ABllingService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PAYMENT_TOPIC = "payment-events";
    private RazorpayClient razorpayClient;

    @Value("${razorpay.key.secret}")
    private String secret;

    @Autowired
    public ABllingService(BillRepository billRepository,
                          KafkaTemplate<String, Object> kafkaTemplate,
                          @Value("${razorpay.key.id}") String key,
                          @Value("${razorpay.key.secret}") String secret) throws Exception {

        this.billRepository = billRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.secret = secret;
        this.razorpayClient = new RazorpayClient(key, secret);
    }

    public Map<String, Object> createOrder(BillData data) throws Exception {
        JSONObject options = new JSONObject();
        options.put("amount", (int) (data.getTotalAmount() * 100));
        options.put("currency", "INR");
        options.put("receipt", UUID.randomUUID().toString());

        Order order = razorpayClient.orders.create(options);

        Map<String, Object> response = new HashMap<>();
        response.put("id", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));
        response.put("receipt", order.get("receipt"));
        response.put("status", order.get("status"));

        return response;
    }

    /**
     * Verify payment and hand off confirmation to Kafka
     */
    public String verifyPayment(Map<String, Object> payload) throws Exception {
        String razorpayOrderId = (String) payload.get("razorpayOrderId");
        String razorpayPaymentId = (String) payload.get("razorpayPaymentId");
        String razorpaySignature = (String) payload.get("razorpaySignature");

        if (razorpayOrderId == null || razorpaySignature == null) {
            return "Invalid Payment Data";
        }

        JSONObject json = new JSONObject();
        json.put("razorpay_order_id", razorpayOrderId);
        json.put("razorpay_payment_id", razorpayPaymentId);
        json.put("razorpay_signature", razorpaySignature);

        boolean valid = Utils.verifyPaymentSignature(json, secret);
        if (!valid) {
            return "Invalid Signature";
        }

        // Extract context payload safely
        String patientName = String.valueOf(payload.get("patientName"));
        String patientId = String.valueOf(payload.get("patientId"));
        String doctorId = String.valueOf(payload.get("doctorId"));
        String appointmentDate = String.valueOf(payload.get("appointmentDate"));
        String timeSlot = String.valueOf(payload.get("timeSlot"));

        // SAFE NUMERIC PARSING (Handles "180.0", 180, 180.0, etc.)
        Object amountObj = payload.get("totalAmount");
        long totalAmount = 0;
        if (amountObj instanceof Number) {
            totalAmount = Math.round(((Number) amountObj).doubleValue());
        } else if (amountObj != null) {
            totalAmount = Math.round(Double.parseDouble(String.valueOf(amountObj)));
        }

        // 1. Save Local Bill
        Bill bill = new Bill();
        bill.setPatientId(Long.parseLong(patientId));
        bill.setBillDate(LocalDate.now());
        bill.setTotalAmount(totalAmount);
        bill.setPaymentStatus(PaymentStatus.PAID);
        billRepository.save(bill);

        // 2. Build Kafka Event Object
        PaymentSuccessEvent event = new PaymentSuccessEvent(
            razorpayOrderId, razorpayPaymentId, patientName, 
            patientId, doctorId, appointmentDate, timeSlot, totalAmount
        );

        // 3. Emit message to Kafka
        kafkaTemplate.send(PAYMENT_TOPIC, patientId, event);

        return "Payment Success";
    }
}