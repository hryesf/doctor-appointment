package com.blubank.doctorappointment.service;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class DoctorDTO {

    private String fullName;
    private String medicalCode;
    private LocalDateTime createdAt;

}
