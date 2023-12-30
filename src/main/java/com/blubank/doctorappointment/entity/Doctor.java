package com.blubank.doctorappointment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
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

    @JsonIgnore
    @OneToMany(mappedBy = "doctor",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true)
    private Set<Appointment> appointmentList = new HashSet<>();

    public Doctor(String fullName, String medicalCode) {
        this.fullName = fullName;
        this.medicalCode = medicalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(fullName, doctor.fullName) && Objects.equals(medicalCode, doctor.medicalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, medicalCode);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "fullName='" + fullName + '\'' +
                ", medicalCode='" + medicalCode + '\'' +
                '}';
    }
}
