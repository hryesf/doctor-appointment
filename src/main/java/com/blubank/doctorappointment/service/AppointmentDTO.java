package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.entity.Patient;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Doctor doctor;
    private Patient patient;
    private LocalDateTime appointmentDateTime;
}
