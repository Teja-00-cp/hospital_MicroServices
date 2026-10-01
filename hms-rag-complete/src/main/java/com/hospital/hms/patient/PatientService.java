// package com.hospital.hms.patient;

// import com.hospital.hms.common.NotFoundException;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;

// @Service
// public class PatientService {

//     private final PatientRepository patientRepository;

//     public PatientService(PatientRepository patientRepository) {
//         this.patientRepository = patientRepository;
//     }

//     @Transactional
//     public PatientResponse create(CreatePatientRequest request) {
//         if (patientRepository.existsByEmail(request.email())) {
//             throw new IllegalArgumentException("A patient with this email already exists");
//         }

//         Patient patient = new Patient(
//                 request.firstName(),
//                 request.lastName(),
//                 request.email(),
//                 request.phoneNumber(),
//                 request.dateOfBirth()
//         );

//         return PatientResponse.from(patientRepository.save(patient));
//     }

//     @Transactional(readOnly = true)
//     public PatientResponse get(Long id) {
//         return PatientResponse.from(findEntity(id));
//     }

//     @Transactional(readOnly = true)
//     public List<PatientResponse> list() {
//         return patientRepository.findAll().stream()
//                 .map(PatientResponse::from)
//                 .toList();
//     }

//     @Transactional(readOnly = true)
//     public Patient findEntity(Long id) {
//         return patientRepository.findById(id)
//                 .orElseThrow(() -> new NotFoundException("Patient " + id + " was not found"));
//     }
// }
