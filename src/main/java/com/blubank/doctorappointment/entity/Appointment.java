package com.blubank.doctorappointment.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity(name = "Appointment")
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("doctorId")
    @JoinColumn(name = "doctor_id",
            foreignKey = @ForeignKey( name = "appointment_doctor_id_fk"),
            nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("patientId")
    @JoinColumn(name = "patient_id",
            foreignKey = @ForeignKey( name = "appointment_patient_id_fk"))
    private Patient patient;

    @Future
    @Column(name = "appointment_dateTime")
    private LocalDateTime appointmentDateTime;

    public Appointment(Doctor doctor, LocalDateTime appointmentDateTime) {
        this.doctor = doctor;
        this.appointmentDateTime = appointmentDateTime;
    }
}
