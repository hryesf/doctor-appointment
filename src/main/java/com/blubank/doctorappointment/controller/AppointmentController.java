package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.entity.Appointment;
import com.blubank.doctorappointment.exception.InvalidStartAndEndTimeException;
import com.blubank.doctorappointment.exception.NotFoundException;
import com.blubank.doctorappointment.exception.TakenAppointmentException;
import com.blubank.doctorappointment.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    ResponseEntity<List<Appointment>> getAllAppointments() {
        return new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping(path = "/{appointment_id}")
    ResponseEntity<Appointment> getAppointmentsById(@PathVariable("appointment_id") Long appointmentId) {
        try {
            return new ResponseEntity<>(appointmentService.getAppointmentById(appointmentId), HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @GetMapping(path = "/select-appointment")
    ResponseEntity<Appointment> takeOpenAppointment(@RequestParam LocalDateTime dateTime, @RequestParam String patientPhoneNumber) {
        try {
            return new ResponseEntity<>(appointmentService.takeOpenAppointment(dateTime, patientPhoneNumber), HttpStatus.OK);
        } catch (TakenAppointmentException e) {
            throw new TakenAppointmentException();
        }
    }

    @GetMapping(path = "/patient/{patient_id}")
    ResponseEntity<List<Appointment>> getAppointmentsByPatientId(@PathVariable("patient_id") Long patientId) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByPatientId(patientId), HttpStatus.OK);
    }

    @GetMapping(path = "/open-appointments/{date}")
    ResponseEntity<List<Appointment>> getOpenAppointments(@PathVariable("date") LocalDate date) {
        return new ResponseEntity<>(appointmentService.getOpenAppointments(date), HttpStatus.OK);
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
