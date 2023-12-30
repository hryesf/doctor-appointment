package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AppointmentConverter {

    public AppointmentDTO toDto(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctor(appointment.getDoctor());
        dto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        dto.setPatient(appointment.getPatient());
        return dto;
    }

    public Page<AppointmentDTO> AppointmentDTOPaginated(Page<Appointment> patientList) {
        return patientList.map(this::toDto);
    }

    public Appointment toEntity(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setDoctor(dto.getDoctor());
        appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
        appointment.setPatient(dto.getPatient());
        return appointment;
    }
}
