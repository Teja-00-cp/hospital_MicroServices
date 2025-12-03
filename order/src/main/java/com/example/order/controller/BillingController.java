// package com.example.order.controller;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import com.example.order.Model.Bill;
// import com.example.order.Service.BillingService;




// @RestController
// @RequestMapping("/order/api/billing")
// public class BillingController {

//     @Autowired
//     private BillingService billingService;

//     @GetMapping("/generate/{id}")
//     public Iterable<Bill> generateBill(@PathVariable Long id) {
//         return billingService.generateBill(id);
//     }

//     @GetMapping("/{billId}")
//     public Bill getBillDetails(@PathVariable Long billId) {
//         return billingService.getBillDetails(billId);
//     }

//     @PostMapping("/pay/add")
//     public String processPayment(@RequestBody BillData billId) {
//     	System.out.println(billId.getPatientId()+"  "+billId.getTotalAmount());
//         return billingService.processPaymentafter(billId);
//     }
//    public  static class BillData {
//         private Long patientId;
//         private Long totalAmount;

//         public Long getPatientId() {
//             return patientId;
//         }

//         public Long getTotalAmount() {
//             return totalAmount;
//         }
//     }
// }