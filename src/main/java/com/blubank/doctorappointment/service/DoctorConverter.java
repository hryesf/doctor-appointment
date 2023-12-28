package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DoctorConverter {
    public DoctorDTO toDto(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setFullName(doctor.getFullName());
        dto.setMedicalCode(doctor.getMedicalCode());
        return dto;
    }

    public Page<DoctorDTO> doctorDTOPaginated(Page<Doctor> patientList) {
        return patientList.map(this::toDto);
    }

    public Doctor toEntity(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setFullName(dto.getMedicalCode());
        doctor.setMedicalCode(dto.getMedicalCode());
        return doctor;
    }
}
