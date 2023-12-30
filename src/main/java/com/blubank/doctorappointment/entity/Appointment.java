package com.blubank.doctorappointment.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "Appointment")
@Table(name = "appointment")
public class Appointment extends BaseEntity {
    @ManyToOne()
    @JoinColumn(foreignKey = @ForeignKey( name = "appointment_doctor_id_fk"))
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey( name = "appointment_patient_id_fk"))
    private Patient patient;

    @Future
    @NotNull
    @NotBlank
    @Column(name = "appointment_dateTime", nullable = false)
    private LocalDateTime appointmentDateTime;

    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_state", nullable = false)
    private AppointmentState appointmentState;

    @Version
    private Long version;

    public Appointment(Doctor doctor, LocalDateTime appointmentDateTime) {
        this.doctor = doctor;
        this.appointmentDateTime = appointmentDateTime;
        this.setCreatedAt(LocalDateTime.now());
        this.setAppointmentState(AppointmentState.AVAILABLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(doctor, that.doctor) && Objects.equals(patient, that.patient) && Objects.equals(appointmentDateTime, that.appointmentDateTime) && appointmentState == that.appointmentState && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctor, patient, appointmentDateTime, appointmentState, version);
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "doctor=" + doctor +
                ", patient=" + patient +
                ", appointmentDateTime=" + appointmentDateTime +
                ", appointmentState=" + getAppointmentState() +
                ", CreatedAt=" + getCreatedAt() +
                '}';
    }
}
