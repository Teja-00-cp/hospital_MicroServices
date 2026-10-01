// package com.hospital.hms.patient;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.List;


// public record PatientResponse(
       
//          Long patientId,

//     String name,

//     LocalDate dateOfBirth,

//     String gender,

//     String contactNumber,

//     String address,

//     String medicalHistory,

//     List<Long> appointmentIds

// ) {
//     public static PatientResponse from(PatientDto patient) {
//         return new PatientResponse(
//                 patient.patientId(),
//                 patient.name(),
//                 patient.dateOfBirth(),
//                 patient.gender(),
//                 patient.contactNumber(),
//                 patient.address(),
//                 patient.medicalHistory(),
//                 patient.appointmentIds()
//         );
//     }
// }
