package com.blubank.doctorappointment.repository;

import com.blubank.doctorappointment.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findAll(Pageable pageable);

    Optional<Patient> findPatientByPhoneNumber(String phoneNumber);

}
