package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.exception.DuplicateDoctorException;
import com.blubank.doctorappointment.exception.NotFoundException;
import com.blubank.doctorappointment.exception.TakenMedicalCodeException;
import com.blubank.doctorappointment.repository.DoctorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorConverter doctorConverter;

    public DoctorService(DoctorRepository doctorRepository, DoctorConverter doctorConverter) {
        this.doctorRepository = doctorRepository;
        this.doctorConverter = doctorConverter;
    }

    public Page<DoctorDTO> getAllDoctors(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return doctorConverter.doctorDTOPaginated(doctorRepository.findAll(pageRequest));
    }

    public DoctorDTO getDoctorById(Long id) {
        return doctorConverter.toDto(doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor with id = " + id + " not found!")));
    }

    public DoctorDTO saveDoctor(Doctor doctor) {

        String medicalCode = doctor.getMedicalCode();
        String fullName = doctor.getFullName();

        doctorRepository.findByMedicalCode(medicalCode)
                .ifPresent(existingDoctor -> {
                    if (existingDoctor.getFullName().equals(fullName)) {
                        throw new DuplicateDoctorException("Doctor with name \"" + fullName + "\" and medical code \"" + medicalCode + "\" already exists!");
                    } else {
                        throw new TakenMedicalCodeException();
                    }
                });

        doctor.setCreatedAt(LocalDateTime.now());
        return doctorConverter.toDto(doctorRepository.save(doctor));

    }

    public String deleteDoctorById(Long id) {

        if (!doctorRepository.existsById(id)) {
            throw new NotFoundException("Doctor with id = " + id + " not found!");
        }
        doctorRepository.deleteById(id);
        return "Doctor with code = " + id + " removed";

    }
}