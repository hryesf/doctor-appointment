package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.exception.*;
import com.blubank.doctorappointment.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientConverter patientConverter;

    public PatientService(PatientRepository patientRepository, PatientConverter patientConverter) {
        this.patientRepository = patientRepository;
        this.patientConverter = patientConverter;
    }

    public Page<PatientDTO> getAllPatients(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return patientConverter.PatientDTOPaginated(patientRepository.findAll(pageRequest));
    }

    public PatientDTO getPatientById(Long id) {
        return patientConverter.toDto(patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient with id = " + id + " not found!")));
    }

    public PatientDTO getPatientByPhoneNumber(String phoneNumber) {
        return patientConverter.toDto(patientRepository.findPatientByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("Patient with phone number \" " + phoneNumber + "\" not Found")));
    }

    public PatientDTO savePatient(Patient patient) {

        if (patient.getFullName().isEmpty()){
            throw new EmptyFullNameException();

        } else if (patient.getPhoneNumber().isEmpty()) {
            throw new EmptyPhoneNumberException();

        }else {

            String phoneNumber = patient.getPhoneNumber();
            String fullName = patient.getFullName();

            Optional<Patient> patientOptional = patientRepository.findPatientByPhoneNumber(phoneNumber);

            if (patientOptional.isPresent()) {
                if (patientOptional.get().getFullName().equals(fullName)) {
                    throw new DuplicatePatientException("Patient with name \"" + fullName + "\" and phone number \"" + phoneNumber + "\" is already registered!");
                } else {
                    throw new TakenPhoneNumberException();
                }
            } else {
                return patientConverter.toDto(patientRepository.save(patient));
            }
        }
    }

    public String deletePatientById(Long id) {
        if (patientRepository.findById(id).isPresent()){
            patientRepository.deleteById(id);
            return "Patient with code " + id + " removed";
        }else {
            throw new NotFoundException("Patient with id =  " + id + " not found!");
        }
    }


}
