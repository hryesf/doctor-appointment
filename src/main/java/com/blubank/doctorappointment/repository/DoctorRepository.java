package com.blubank.doctorappointment.repository;

import com.blubank.doctorappointment.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByMedicalCode(String medicalCode);
    boolean existsByMedicalCode(String medicalCode);
    boolean existsByFullName(String fullName);
}
