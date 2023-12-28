package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.exception.*;
import com.blubank.doctorappointment.service.AppointmentDTO;
import com.blubank.doctorappointment.service.AppointmentService;
import com.blubank.doctorappointment.service.PatientDTO;
import com.blubank.doctorappointment.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "api/v1/patients")
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientController(PatientService patientService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    ResponseEntity<Page<PatientDTO>> getAllPatients(@RequestParam int page, @RequestParam int size) {
        Page<PatientDTO> patients = patientService.getAllPatients(page, size);

        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping(path = "/{patient_id}")
    ResponseEntity<PatientDTO> getPatientById(@PathVariable("patient_id") Long id) {
        try {
            return new ResponseEntity<>(patientService.getPatientById(id), HttpStatus.OK);

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @GetMapping(path = "/patient-appointments/{phone_number}")
    ResponseEntity<Page<AppointmentDTO>> getPatientAppointments(@PathVariable("phone_number") String phoneNumber,
                                                                @RequestParam int page,
                                                                @RequestParam int size) {
        try {
            PatientDTO patientDTO = patientService.getPatientByPhoneNumber(phoneNumber);
            return new ResponseEntity<>(appointmentService.getAppointmentsByPatientPhoneNumber(patientDTO.getPhoneNumber(), page, size),
                    HttpStatus.OK);

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PostMapping
    ResponseEntity<PatientDTO> savePatient(@Valid @RequestBody Patient newPatient) {
        try {
            return new ResponseEntity<>(patientService.savePatient(newPatient), HttpStatus.OK);

        } catch (EmptyFullNameException e) {
            throw new EmptyFullNameException();

        } catch (EmptyPhoneNumberException e2) {
            throw new EmptyPhoneNumberException();

        } catch (DuplicatePatientException e3) {
            throw new DuplicatePatientException(e3.getMessage());

        } catch (TakenPhoneNumberException e4) {
            throw new TakenPhoneNumberException();
        }
    }

    @DeleteMapping(path = "/{patient_id}")
    ResponseEntity<String> deletePatientById(@PathVariable("patient_id") Long id) {
        try {
            return new ResponseEntity<>(patientService.deletePatientById(id), HttpStatus.OK);

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    // related tp Appointment
    @GetMapping(path = "/patient/{patient_phoneNumber}")
    ResponseEntity<Page<AppointmentDTO>> getAppointmentsByPatientPhoneNumber(
            @PathVariable("patient_phoneNumber") String phoneNumber,
            @RequestParam int page,
            @RequestParam int size) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByPatientPhoneNumber(phoneNumber, page, size),
                HttpStatus.OK);
    }

    @GetMapping(path = "/open-appointments/{date}")
    ResponseEntity<Page<AppointmentDTO>> getOpenAppointments(@PathVariable("date") LocalDate date,
                                                          @RequestParam int page,
                                                          @RequestParam int size) {
        return new ResponseEntity<>(appointmentService.getOpenAppointments(date, page, size),
                HttpStatus.OK);
    }

    @GetMapping(path = "/select-appointment")
    ResponseEntity<AppointmentDTO> takeOpenAppointment(@RequestParam LocalDateTime dateTime,
                                                    @RequestParam String patientPhoneNumber) {
        try {
            return new ResponseEntity<>(appointmentService.takeOpenAppointment(dateTime, patientPhoneNumber),
                    HttpStatus.OK);

        } catch (TakenAppointmentException e) {
            throw new TakenAppointmentException();
        }
    }

}
