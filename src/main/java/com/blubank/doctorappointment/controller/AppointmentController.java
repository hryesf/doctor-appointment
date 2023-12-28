package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.entity.Appointment;
import com.blubank.doctorappointment.exception.InvalidStartAndEndTimeException;
import com.blubank.doctorappointment.exception.NotFoundException;
import com.blubank.doctorappointment.exception.TakenAppointmentException;
import com.blubank.doctorappointment.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "api/v1/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    ResponseEntity<Page<Appointment>> getAllAppointments(@RequestParam int page,
                                                         @RequestParam int size) {
        return new ResponseEntity<>(appointmentService.getAllAppointments(page, size), HttpStatus.OK);
    }

    @GetMapping(path = "/{appointment_id}")
    ResponseEntity<Appointment> getAppointmentById(@PathVariable("appointment_id") Long appointmentId) {
        try {
            return new ResponseEntity<>(appointmentService.getAppointmentById(appointmentId), HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @GetMapping(path = "/select-appointment")
    ResponseEntity<Appointment> takeOpenAppointment(@RequestParam LocalDateTime dateTime,
                                                    @RequestParam String patientPhoneNumber) {
        try {
            return new ResponseEntity<>(appointmentService.takeOpenAppointment(dateTime, patientPhoneNumber), HttpStatus.OK);
        } catch (TakenAppointmentException e) {
            throw new TakenAppointmentException();
        }
    }

    @GetMapping(path = "/patient/{patient_id}")
    ResponseEntity<Page<Appointment>> getAppointmentsByPatientId(@PathVariable("patient_id") Long patientId,
                                                                 @RequestParam int page,
                                                                 @RequestParam int size) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByPatientId(patientId, page, size), HttpStatus.OK);
    }

    @GetMapping(path = "/open-appointments/{date}")
    ResponseEntity<Page<Appointment>> getOpenAppointments(@PathVariable("date") LocalDate date,
                                                          @RequestParam int page,
                                                          @RequestParam int size) {
        return new ResponseEntity<>(appointmentService.getOpenAppointments(date, page, size), HttpStatus.OK);
    }

    @PostMapping
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

}
