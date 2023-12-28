package com.blubank.doctorappointment.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity(name = "Patient")
@Table(name = "patient")
public class Patient extends BaseEntity{

    @NotNull(message = "Full name is required!")
    @NotBlank(message = "Full name can not be empty!")
    @Column(name = "patient_fullName",
            columnDefinition = "varchar(50) default 'unknown'")
    private String fullName;

    @NotNull(message = "Phone number is required!")
    @NotBlank(message = "Phone number can not be empty!")
    @Pattern(regexp = "^09\\d{9}$", message = "invalid mobile number entered ")
    @Digits(integer = 12, fraction = 0,
            message = "The phone number is not correct!")
    @Column(name = "phone_number",
            columnDefinition = "varchar(20)",
            unique = true,
            nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Appointment> appointmentList = new HashSet<>();

}
