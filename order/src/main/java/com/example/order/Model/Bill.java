// package com.example.order.Model;
// import java.time.LocalDate;

// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;

// @Entity
// public class Bill {

// 	@Id
// 	@GeneratedValue(strategy = GenerationType.IDENTITY)
// 	private Long billId;

// 	@ManyToOne
// 	@JoinColumn(name = "patient_id") // This specifies the foreign key column in the Bill table
// 	private Patient patient;

// 	private long totalAmount;

// 	@Enumerated(EnumType.STRING)
// 	private PaymentStatus paymentStatus;
// 	public Bill() {
// 		super();
// 		// TODO Auto-generated constructor stub
// 	}

// 	public Bill(Long billId, Patient patient, long totalAmount, PaymentStatus paymentStatus, LocalDate billDate) {
// 		super();
// 		this.billId = billId;
// 		this.patient = patient;
// 		this.totalAmount = totalAmount;
// 		this.paymentStatus = paymentStatus;
// 		this.billDate = billDate;
// 	}

// 	public Long getBillId() {
// 		return billId;
// 	}

// 	public void setBillId(Long billId) {
// 		this.billId = billId;
// 	}

// 	public Patient getPatient() {
// 		return patient;
// 	}

// 	public void setPatient(Patient patient) {
// 		this.patient = patient;
// 	}

// 	public long getTotalAmount() {
// 		return totalAmount;
// 	}

// 	public void setTotalAmount(long totalAmount) {
// 		this.totalAmount = totalAmount;
// 	}

// 	public PaymentStatus getPaymentStatus() {
// 		return paymentStatus;
// 	}

// 	public void setPaymentStatus(PaymentStatus paymentStatus) {
// 		this.paymentStatus = paymentStatus;
// 	}

// 	public LocalDate getBillDate() {
// 		return billDate;
// 	}

// 	public void setBillDate(LocalDate billDate) {
// 		this.billDate = billDate;
// 	}

// 	public enum PaymentStatus{
// 		PAID,
// 	    UNPAID
// 	}

// 	 private LocalDate billDate;
// }