package com.blubank.doctorappointment.repository;

import com.blubank.doctorappointment.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.patient IS NULL AND DATE(a.appointmentDateTime) = :date")
    Page<Appointment> findOpenAppointments(@Param("date") LocalDate date, PageRequest pageRequest);

    Optional<Appointment> findAppointmentByAppointmentDateTime(LocalDateTime dateTime);
    Page<Appointment> findAppointmentsByPatientId(Long patientId, PageRequest pageRequest);

}
