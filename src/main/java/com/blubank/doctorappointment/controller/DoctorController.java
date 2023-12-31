package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.service.AppointmentDTO;
import com.blubank.doctorappointment.service.AppointmentService;
import com.blubank.doctorappointment.service.DoctorDTO;
import com.blubank.doctorappointment.service.DoctorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "api/v1/doctors")
@Validated
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    ResponseEntity<Page<DoctorDTO>> getAllDoctors(@RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to retrieve all doctors. Size: {}", size);
        Page<DoctorDTO> doctors = doctorService.getAllDoctorsDto(size);
        logger.info("Retrieved {} doctors successfully.", doctors.getTotalElements());
        return ResponseEntity.ok(doctors);
    }

    @GetMapping(path = "/{doctor_id}")
    ResponseEntity<DoctorDTO> getDoctorById(@PathVariable("doctor_id") Long id) {
        logger.info("Received request to retrieve doctor by ID: {}", id);
        DoctorDTO doctorDTO = doctorService.getDoctorDtoById(id);
        logger.info("Retrieved doctor successfully. ID: {}", id);
        return ResponseEntity.ok(doctorDTO);
    }

    @PostMapping
    ResponseEntity<DoctorDTO> saveDoctor(@Valid @RequestBody @NotNull Doctor newDoctor) {
        logger.info("Received request to save a new doctor.");
        DoctorDTO savedDoctor = doctorService.saveDoctor(newDoctor);
        logger.info("Saved new doctor successfully.");
        return ResponseEntity.ok(savedDoctor);
    }

    @DeleteMapping(path = "/{doctor_id}")
    ResponseEntity<String> deleteDoctorById(@PathVariable("doctor_id") Long id) {
        logger.info("Received request to delete doctor by ID: {}", id);
        String resultMessage = doctorService.deleteDoctorById(id);
        logger.info("Deleted doctor successfully. ID: {}", id);
        return ResponseEntity.ok(resultMessage);
    }

    @PostMapping("/add-appointments")
    ResponseEntity<String> saveAppointments(@Valid @RequestBody Doctor doctor,
                                            @Valid @RequestParam @NotNull @NotBlank @Future
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            LocalDateTime startTime,
                                            @Valid @RequestParam @NotNull @NotBlank @Future
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            LocalDateTime endTime) {
        logger.info("Received request to save appointments for doctor with ID: {}, Start Time: {}, End Time: {}",
                doctor.getId(), startTime, endTime);
        String resultMessage = appointmentService.saveAppointments(doctor, startTime, endTime);
        logger.info("Saved appointments successfully. Result: {}", resultMessage);
        return ResponseEntity.ok(resultMessage);
    }

    @DeleteMapping(path = "/delete-appointment/{appointment_id}")
    ResponseEntity<String> deleteAppointmentById(@PathVariable("appointment_id") Long id) {
        logger.info("Received request to delete appointment by ID: {}", id);
        String resultMessage = appointmentService.deleteAppointmentById(id);
        logger.info("Deleted appointment successfully. ID: {}", id);
        return ResponseEntity.ok(resultMessage);
    }

    @GetMapping("/show-appointments")
    ResponseEntity<Page<AppointmentDTO>> getAllAppointments(@RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to retrieve all appointments. Size: {}", size);
        Page<AppointmentDTO> appointments = appointmentService.getAllAppointmentsDto(size);
        logger.info("Retrieved {} appointments successfully.", appointments.getTotalElements());
        return ResponseEntity.ok(appointments);    }
}
