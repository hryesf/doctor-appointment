package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PatientConverter {
    public PatientDTO toDto(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setFullName(patient.getFullName());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setCreatedAt(patient.getCreatedAt());
        return dto;
    }


    public Page<PatientDTO> PatientDTOPaginated(Page<Patient> patientList) {
        return patientList.map(this::toDto);
    }

    public Patient toEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setFullName(dto.getFullName());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setCreatedAt(dto.getCreatedAt());
        return patient;
    }
}
