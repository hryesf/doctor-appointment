package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.service.AppointmentDTO;
import com.blubank.doctorappointment.service.AppointmentService;
import com.blubank.doctorappointment.service.PatientDTO;
import com.blubank.doctorappointment.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "api/v1/patients")
@Validated
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientController(PatientService patientService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    ResponseEntity<Page<PatientDTO>> getAllPatients(@RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to retrieve all patients. Size: {}",size);
        Page<PatientDTO> patients = patientService.getAllPatients(size);
        logger.info("Retrieved {} patients successfully.", patients.getTotalElements());
        return ResponseEntity.ok(patients);
    }

    @GetMapping(path = "/{patient_id}")
    ResponseEntity<PatientDTO> getPatientById(@PathVariable("patient_id") Long id) {
        logger.info("Received request to retrieve patient by ID: {}", id);
        PatientDTO patientDTO = patientService.getPatientById(id);
        logger.info("Retrieved patient successfully. ID: {}", id);
        return ResponseEntity.ok(patientDTO);
    }

    @PostMapping
    ResponseEntity<PatientDTO> savePatient(@Valid @RequestBody @NotNull Patient newPatient) {
        logger.info("Received request to save a new patient.");
        PatientDTO savedPatient = patientService.savePatient(newPatient);
        logger.info("Saved new patient successfully.");
        return ResponseEntity.ok(savedPatient);
    }

    @DeleteMapping(path = "/{patient_id}")
    ResponseEntity<String> deletePatientById(@PathVariable("patient_id") Long id) {
        logger.info("Received request to delete patient by ID: {}", id);
        String resultMessage = patientService.deletePatientById(id);
        logger.info("Deleted patient successfully. ID: {}", id);
        return ResponseEntity.ok(resultMessage);
    }

    @GetMapping(path = "/patient-appointments/{phone_number}")
    ResponseEntity<Page<AppointmentDTO>> getPatientAppointments(@PathVariable("phone_number") String phoneNumber,
                                                                @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to retrieve appointments for patient with phone number: {}", phoneNumber);
        PatientDTO patientDTO = patientService.getPatientByPhoneNumber(phoneNumber);
        Page<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatientPhoneNumber(patientDTO.getPhoneNumber(), size);
        logger.info("Retrieved {} appointments for patient with phone number: {}", appointments.getTotalElements(), phoneNumber);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping(path = "/available-appointments/{date}")
    ResponseEntity<Page<AppointmentDTO>> getOpenAppointments(@PathVariable("date")
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                             LocalDateTime date,
                                                             @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to retrieve available appointments for date: {}", date);
        Page<AppointmentDTO> openAppointments = appointmentService.getOpenAppointments(date, size);
        logger.info("Retrieved {} available appointments for date: {}", openAppointments.getTotalElements(), date);
        return ResponseEntity.ok(openAppointments);
    }

    @PutMapping(path = "/select-appointment")
    ResponseEntity<AppointmentDTO> takeOpenAppointment(@Valid @RequestParam @NotNull Long id,
                                                       @Valid @RequestParam @NotNull @NotBlank String patientPhoneNumber) {
        logger.info("Received request to take an open appointment. Appointment ID: {}, Patient Phone Number: {}", id, patientPhoneNumber);
        AppointmentDTO takenAppointment = appointmentService.takeOpenAppointment(id, patientPhoneNumber);
        logger.info("Took open appointment successfully. ID: {}, Patient Phone Number: {}", id, patientPhoneNumber);
        return ResponseEntity.ok(takenAppointment);
    }

}
