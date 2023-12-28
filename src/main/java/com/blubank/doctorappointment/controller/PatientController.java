package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.service.AppointmentDTO;
import com.blubank.doctorappointment.service.AppointmentService;
import com.blubank.doctorappointment.service.PatientDTO;
import com.blubank.doctorappointment.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    ResponseEntity<Page<PatientDTO>> getAllPatients(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Page<PatientDTO> patients = patientService.getAllPatients(page, size);
        return ResponseEntity.ok(patients);
    }

    @GetMapping(path = "/{patient_id}")
    ResponseEntity<PatientDTO> getPatientById(@PathVariable("patient_id") Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping(path = "/patient-appointments/{phone_number}")
    ResponseEntity<Page<AppointmentDTO>> getPatientAppointments(@PathVariable("phone_number") String phoneNumber,
                                                                @RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        PatientDTO patientDTO = patientService.getPatientByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientPhoneNumber(patientDTO.getPhoneNumber(), page, size));
    }

    @PostMapping
    ResponseEntity<PatientDTO> savePatient(@Valid @RequestBody @NotNull Patient newPatient) {
        return ResponseEntity.ok(patientService.savePatient(newPatient));
    }

    @DeleteMapping(path = "/{patient_id}")
    ResponseEntity<String> deletePatientById(@PathVariable("patient_id") Long id) {
        return ResponseEntity.ok(patientService.deletePatientById(id));
    }

    // related to Appointment
    @GetMapping(path = "/patient/{patient_phoneNumber}")
    ResponseEntity<Page<AppointmentDTO>> getAppointmentsByPatientPhoneNumber(
            @PathVariable("patient_phoneNumber") String phoneNumber,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientPhoneNumber(phoneNumber, page, size));
    }

    @GetMapping(path = "/open-appointments/{date}")
    ResponseEntity<Page<AppointmentDTO>> getOpenAppointments(@PathVariable("date") LocalDate date,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appointmentService.getOpenAppointments(date, page, size));
    }

    @GetMapping(path = "/select-appointment")
    ResponseEntity<AppointmentDTO> takeOpenAppointment(@Valid @RequestParam @Future LocalDateTime dateTime,
                                                       @Valid @RequestParam @NotNull @NotBlank String patientPhoneNumber) {
        return ResponseEntity.ok(appointmentService.takeOpenAppointment(dateTime, patientPhoneNumber));
    }

}
