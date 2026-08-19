package com.example.bill.DTO;
public class BillData {
        private String patientName;
        private long totalAmount; // Changed to Double to handle decimals like "150.00"

        // GETTERS
        public String getPatientName() {
            return patientName;
        }

        public Long getTotalAmount() {
            return totalAmount;
        }

        // SETTERS (These are required for Spring to map the JSON!)
        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public void setTotalAmount(Long totalAmount) {
            this.totalAmount = totalAmount;
        }
    }