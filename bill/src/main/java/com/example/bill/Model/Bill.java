package com.example.bill.Model;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Bill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long billId;

	private long patientId;

	private long totalAmount;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	public Bill() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Bill(Long billId, long patientId, long totalAmount, PaymentStatus paymentStatus, LocalDate billDate) {
		super();
		this.billId = billId;
		this.patientId = patientId;
		this.totalAmount = totalAmount;
		this.paymentStatus = paymentStatus;
		this.billDate = billDate;
	}

	public Long getBillId() {
		return billId;
	}

	public long getPatientId() {
		return patientId;
	}

	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}

	

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDate getBillDate() {
		return billDate;
	}

	public void setBillDate(LocalDate billDate) {
		this.billDate = billDate;
	}

	public enum PaymentStatus{
		PAID,
	    UNPAID
	}

	 private LocalDate billDate;
}