// package com.hospital.hms.patient;

// import jakarta.validation.Valid;
// import org.springframework.http.HttpStatus;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.bind.annotation.RestController;

// import java.util.List;

// @RestController
// @RequestMapping("/api/v1/patients")
// public class PatientController {

//     private final PatientService patientService;

//     public PatientController(PatientService patientService) {
//         this.patientService = patientService;
//     }

//     @PostMapping
//     @ResponseStatus(HttpStatus.CREATED)
//     public PatientResponse create(@Valid @RequestBody CreatePatientRequest request) {
//         return patientService.create(request);
//     }

//     @GetMapping("/{id}")
//     public PatientResponse get(@PathVariable Long id) {
//         return patientService.get(id);
//     }

//     @GetMapping
//     public List<PatientResponse> list() {
//         return patientService.list();
//     }
// }
