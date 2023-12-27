package com.blubank.doctorappointment.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity(name = "Doctor")
@Table(name = "doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Name must not be empty!")
    @Column(name = "fullName",
            columnDefinition = "varchar(50) default 'unknown'")
    private String fullName;

    @NotBlank(message = "Medical Code must not be empty!")
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
