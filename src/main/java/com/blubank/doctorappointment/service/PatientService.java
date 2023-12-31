package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.exception.DuplicatePatientException;
import com.blubank.doctorappointment.exception.NotFoundException;
import com.blubank.doctorappointment.exception.TakenPhoneNumberException;
import com.blubank.doctorappointment.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final PatientConverter patientConverter;

    public PatientService(PatientRepository patientRepository, PatientConverter patientConverter) {
        this.patientRepository = patientRepository;
        this.patientConverter = patientConverter;
    }

    public Page<PatientDTO> getAllPatientsDto(int size) {
        Pageable pageable = Pageable.ofSize(size);
        return patientConverter.PatientDTOPaginated(patientRepository.findAll(pageable));
    }

    public PatientDTO getPatientDtoById(Long id) {
        return patientConverter.toDto(patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient with id = " + id + " not found!")));
    }


    public PatientDTO getPatientDtoByPhoneNumber(String phoneNumber) {
        return patientConverter.toDto(patientRepository.findPatientByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("Patient with phone number \"" + phoneNumber + "\" not Found")));
    }

    public Patient getPatientByPhoneNumber(String phoneNumber) {
        return patientRepository.findPatientByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("Patient with phone number \"" + phoneNumber + "\" not Found"));
    }


    @Transactional
    public PatientDTO savePatient(Patient patient) {
        String phoneNumber = patient.getPhoneNumber();
        String fullName = patient.getFullName();

        patientRepository.findPatientByPhoneNumber(phoneNumber)
                .ifPresent(existingPatient -> {
                    if (existingPatient.getFullName().equals(fullName)) {
                        logger.error("DuplicatePatientException: Patient with name \"{}\" and phone number \"{}\" is already registered!", fullName, phoneNumber);
                        throw new DuplicatePatientException("Patient with name \"" + fullName + "\" and phone number \"" + phoneNumber + "\" is already registered!");
                    } else {
                        logger.error("TakenPhoneNumberException: Phone number \"{}\" is already associated with another patient!", phoneNumber);
                        throw new TakenPhoneNumberException();
                    }
                });

        patient.setCreatedAt(LocalDateTime.now());
        return patientConverter.toDto(patientRepository.save(patient));
    }


    @Transactional
    public String deletePatientById(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return "Patient with code " + id + " removed";
        } else {
            throw new NotFoundException("Patient with id =  " + id + " not found!");
        }
    }


}
