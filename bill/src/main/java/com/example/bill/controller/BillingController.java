package com.example.bill.controller;
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