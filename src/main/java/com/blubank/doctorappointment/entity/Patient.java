package com.blubank.doctorappointment.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
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
    @Column(name = "fullName",
            columnDefinition = "varchar(50) default 'unknown'")
    private String fullName;

    @NaturalId
    @NotNull(message = "Phone number is required!")
    @NotBlank(message = "Phone number can not be empty!")
    @Pattern(regexp = "^09\\d{9}$", message = "invalid phone number entered ")
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
