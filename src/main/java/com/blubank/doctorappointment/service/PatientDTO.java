package com.blubank.doctorappointment.service;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientDTO {

    private String fullName;
    private String phoneNumber;
    private LocalDateTime createdAt;

}
