package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.exception.*;
import com.blubank.doctorappointment.service.AppointmentDTO;
import com.blubank.doctorappointment.service.AppointmentService;
import com.blubank.doctorappointment.service.DoctorDTO;
import com.blubank.doctorappointment.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "api/v1/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    ResponseEntity<Page<DoctorDTO>> getAllDoctors(@RequestParam int page, @RequestParam int size) {
        return new ResponseEntity<>(doctorService.getAllDoctors(page, size), HttpStatus.OK);
    }

    @GetMapping(path = "/{doctor_id}")
    ResponseEntity<DoctorDTO> getDoctorById(@PathVariable("doctor_id") Long id) {
        try {
            return new ResponseEntity<>(doctorService.getDoctorById(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PostMapping
    ResponseEntity<DoctorDTO> saveDoctor(@Valid @RequestBody Doctor newDoctor) {
        try {
            return new ResponseEntity<>(doctorService.saveDoctor(newDoctor), HttpStatus.OK);

        } catch (DuplicateDoctorException e) {
            throw new DuplicateDoctorException(e.getMessage());

        } catch (TakenMedicalCodeException e2) {
            throw new TakenMedicalCodeException();
        }
    }

    @DeleteMapping(path = "/{doctor_id}")
    ResponseEntity<String> deleteDoctorById(@PathVariable("doctor_id") Long id) {
        try {
            return new ResponseEntity<>(doctorService.deleteDoctorById(id), HttpStatus.OK);

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());

        }
    }

    // related to Appointments
    @PostMapping("/add-appointments")
    ResponseEntity<String> saveAppointments(@RequestParam Long doctorId,
                                            LocalDateTime startTime,
                                            LocalDateTime endTime) {
        try {
            return new ResponseEntity<>(appointmentService.saveAppointments(doctorId, startTime, endTime),
                    HttpStatus.OK);

        } catch (InvalidStartAndEndTimeException e) {
            throw new InvalidStartAndEndTimeException();
        }
    }


    @DeleteMapping(path = "/{appointment_id}")
    ResponseEntity<String> deleteAppointmentById(@PathVariable("appointment_id") Long id) {
        try {
            return new ResponseEntity<>(appointmentService.deleteAppointmentById(id), HttpStatus.OK);

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());

        } catch (TakenAppointmentException e2) {
            throw new TakenAppointmentException();
        }
    }

    @GetMapping
    ResponseEntity<Page<AppointmentDTO>> getAllAppointments(@RequestParam int page,
                                                            @RequestParam int size) {
        return new ResponseEntity<>(appointmentService.getAllAppointments(page, size), HttpStatus.OK);
    }
}
