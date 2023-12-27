package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.exception.*;
import com.blubank.doctorappointment.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient with id = " + id + " not found!"));
    }

    public Patient getPatientByPhoneNumber(String phoneNumber) {
        return patientRepository.findPatientByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("Patient with phone number \" " + phoneNumber + "\" not Found"));
    }

    public Patient savePatient(Patient patient) {

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
                return patientRepository.save(patient);
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
