package com.blubank.doctorappointment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
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

    @JsonIgnore
    @OneToMany(mappedBy = "patient",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true)
    private Set<Appointment> appointmentList = new HashSet<>();

    public Patient(String fullName, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(fullName, patient.fullName) && Objects.equals(phoneNumber, patient.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, phoneNumber);
    }


}
