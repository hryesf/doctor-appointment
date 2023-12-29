package com.blubank.doctorappointment.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity(name = "Appointment")
@Table(name = "appointment")
public class Appointment extends BaseEntity {

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

    @NotNull
    @NotBlank
    @Column(name = "appointment_state")
    private AppointmentState appointmentState;

    @Version
    private Long version;

    public Appointment(Doctor doctor, LocalDateTime appointmentDateTime) {
        this.doctor = doctor;
        this.appointmentDateTime = appointmentDateTime;
        this.setCreatedAt(LocalDateTime.now());
        this.setAppointmentState(AppointmentState.AVAILABLE);
    }
}
