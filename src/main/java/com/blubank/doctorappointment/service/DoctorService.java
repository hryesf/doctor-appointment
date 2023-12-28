package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.exception.DuplicateDoctorException;
import com.blubank.doctorappointment.exception.NotFoundException;
import com.blubank.doctorappointment.exception.TakenMedicalCodeException;
import com.blubank.doctorappointment.repository.DoctorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Page<Doctor> getAllDoctors(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return doctorRepository.findAll(pageRequest);
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor with id = " + id + " not found!"));
    }

    public Doctor saveDoctor(Doctor doctor) throws DuplicateDoctorException, TakenMedicalCodeException {

        String medicalCode = doctor.getMedicalCode();
        String fullName = doctor.getFullName();

        Optional<Doctor> doctorOptional = doctorRepository.findByMedicalCode(medicalCode);
        if (doctorOptional.isPresent()) {
            if (doctorOptional.get().getFullName().equals(fullName)) {
                throw new DuplicateDoctorException("Doctor with name \"" + fullName + "\" and medical code \"" + medicalCode + "\" is already exists!");
            } else {
                throw new TakenMedicalCodeException();
            }
        } else {
            return doctorRepository.save(doctor);
        }
    }

    public String deleteDoctorById(Long id) {
        if (doctorRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Appointment with id = " + id + " not found!");
        }else {
            doctorRepository.deleteById(id);
            return "Doctor with code = " + id + " removed";
        }
    }
}
