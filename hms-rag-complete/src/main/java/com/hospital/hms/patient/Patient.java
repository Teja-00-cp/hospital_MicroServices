// package com.hospital.hms.patient;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;

// import java.time.LocalDate;
// import java.time.LocalDateTime;

// @Entity
// @Table(name = "patients")
// public class Patient {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "first_name", nullable = false, length = 50)
//     private String firstName;

//     @Column(name = "last_name", nullable = false, length = 50)
//     private String lastName;

//     @Column(nullable = false, unique = true, length = 100)
//     private String email;

//     @Column(name = "phone_number", length = 20)
//     private String phoneNumber;

//     @Column(name = "date_of_birth", nullable = false)
//     private LocalDate dateOfBirth;

//     @Column(name = "created_at", nullable = false)
//     private LocalDateTime createdAt;

//     protected Patient() {
//     }

//     public Patient(String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth) {
//         this.firstName = firstName;
//         this.lastName = lastName;
//         this.email = email;
//         this.phoneNumber = phoneNumber;
//         this.dateOfBirth = dateOfBirth;
//         this.createdAt = LocalDateTime.now();
//     }

//     public Long getId() {
//         return id;
//     }

//     public String getFirstName() {
//         return firstName;
//     }

//     public String getLastName() {
//         return lastName;
//     }

//     public String getEmail() {
//         return email;
//     }

//     public String getPhoneNumber() {
//         return phoneNumber;
//     }

//     public LocalDate getDateOfBirth() {
//         return dateOfBirth;
//     }

//     public LocalDateTime getCreatedAt() {
//         return createdAt;



//     }  
// }
