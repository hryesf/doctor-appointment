/*
package com.blubank.doctorappointment.repository;

import com.blubank.doctorappointment.entity.Appointment;
import com.blubank.doctorappointment.entity.Doctor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class AppointmentRepositoryTest {
    */
/*@Autowired
    private DoctorRepository doctorRepository;*//*

    @Autowired
    private AppointmentRepository underTest;

    */
/*@AfterEach
    void tearDown() {
        underTest.deleteAll();
    }*//*


    @Test
    @Transactional
    void itShouldFindOpenAppointments() {
        // Given
       */
/* Doctor doctor = new Doctor("John Green", "6743682476");
        doctor.setId(16L);
        doctorRepository.save(doctor);
        System.out.println(doctor.getId());
        System.out.println(doctor.toString());

        LocalDateTime dateTime = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Appointment appointment = new Appointment(doctor, dateTime);
        appointment.setId(19L);
        System.out.println((appointment.toString()));

        System.out.println("////////////////////////////////////////");
        List savedDoctor = doctorRepository.findAll();
        System.out.println(savedDoctor);*//*

        PageRequest pageRequest = PageRequest.of(0, 10);
        LocalDateTime dateTime = LocalDateTime.now();
        Doctor doctor = new Doctor("fdfdg", "1111111111");
        doctor.setId(12L);
        Appointment appointment = new Appointment(doctor,dateTime);
        appointment.setId(13L);
        underTest.save(appointment);

        // When
        Page<Appointment> result = underTest.findOpenAppointments(dateTime, pageRequest);

        // Then
        // Add assertions based on your test data and expectations
        assertEquals(1, result.getContent().size());  // Assuming no open appointments at the current time

    }
}*/
