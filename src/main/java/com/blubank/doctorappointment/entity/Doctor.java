package com.blubank.doctorappointment.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Doctor")
@Table(name = "doctor")
public class Doctor extends BaseEntity{

    @NotNull(message = "Full name is required!")
    @NotBlank(message = "Full name can not be empty!")
    @Column(name = "fullName",
            columnDefinition = "varchar(50) default 'unknown'")
    private String fullName;

    @NaturalId
    @NotNull(message = "Medical Code is required!")
    @NotBlank(message = "Medical Code can not be empty!")
    @Digits(integer = 10,
            fraction = 0,
            message = "The medical Code is not correct!")
    @Column(name = "medical_code",
            columnDefinition = "varchar(12)",
            unique = true,
            nullable = false)
    private String medicalCode;

    @OneToMany(mappedBy = "doctor",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Appointment> appointmentList = new HashSet<>();
}
