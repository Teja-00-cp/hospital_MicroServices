package com.hospital.hms.patient;

import java.time.LocalDate;
import java.util.List;

public record PatientDto(

    Long patientId,

    String name,

    LocalDate dateOfBirth,

    String gender,

    String contactNumber,

    String address,

    String medicalHistory,

    List<Long> appointmentIds

) {}