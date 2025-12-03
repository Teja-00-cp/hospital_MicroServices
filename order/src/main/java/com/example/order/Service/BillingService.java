// package com.example.order.Service;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.bind.annotation.PathVariable;

// import com.example.order.Model.Bill;
// import com.example.order.Model.Bill.PaymentStatus;
// import com.example.order.Model.Patient;
// import com.example.order.Repository.BillRepository;
// import com.example.order.Repository.DocRe;
// import com.example.order.Repository.PatientRepository;
// import com.example.order.controller.BillingController.BillData;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;


// import java.time.LocalDate;
// import java.util.Date;
// import java.util.Map;

// @Service
// public class BillingService {

//     @Autowired
//     private BillRepository billRepository;
    
//     @Autowired
//     private PatientRepository patientRepository;

//     @Autowired
//     private DocRe docRe;

//     public Iterable<Bill> generateBill(Long patientId) {

//     	return billRepository.findByPatient_PatientId(patientId);

//     }
    
//     public String processPaymentafter(BillData data) {
//     	String status=processPayment(data);
//     	Bill bill=new Bill();
//     	bill.setBillDate(LocalDate.now());
//     	bill.setPatient(patientRepository.findById(data.getPatientId()).orElse(new Patient()));
//     	bill.setTotalAmount(data.getTotalAmount());
//     	bill.setPaymentStatus(status=="fail"?PaymentStatus.UNPAID:PaymentStatus.PAID);
//     	billRepository.save(bill);
    	
//         return status;
//     }

//     public Bill getBillDetails(Long billId) {
//         return billRepository.findById(billId)
//                 .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + billId));
//     }

//     public String processPayment(BillData data) {
//     	boolean bol=patientRepository.findById(data.getPatientId()).orElse(null)!=null;
    	
    	
//         return System.currentTimeMillis()%2==0 && bol?"success":"fail";
//     }
// }