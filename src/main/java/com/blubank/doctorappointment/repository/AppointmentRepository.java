package com.blubank.doctorappointment.repository;

import com.blubank.doctorappointment.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "0")})
    Optional<Appointment> findById(Long id);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentState = 'AVAILABLE' AND a.appointmentDateTime > :currentDateTime")
    Page<Appointment> findOpenAppointments(@Param("date") LocalDateTime dateTime, PageRequest pageRequest);

    Page<Appointment> findAppointmentsByPatientPhoneNumber(String patientPhoneNumber, PageRequest pageRequest);


}
