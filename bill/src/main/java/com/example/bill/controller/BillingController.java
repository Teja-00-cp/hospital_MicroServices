package com.example.bill.controller;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.bill.Model.Bill;
import com.example.bill.Service.BillingService;





@RestController
@RequestMapping("/bill/api/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

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
            // System.err.println("❌ Wait was interrupted: " + e.getMessage());
        }
		// System.out.println("Load test completed successfully.");
		return "Load test completed successfully -- Bill Service";
	}

    @PostMapping("/pay/add")
    public String processPayment(@RequestBody BillData billId) {
    	System.out.println(billId.getPatientName()+"  "+billId.getTotalAmount());
        return billingService.processPaymentafter(billId);
    }
   public  static class BillData {
        private String patientName;
        private Long totalAmount;
        

        public String getPatientName() {
            return patientName;
        }

        public Long getTotalAmount() {
            return totalAmount;
        }
    }
}