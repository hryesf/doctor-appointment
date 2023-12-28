package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.service.AppointmentDTO;
import com.blubank.doctorappointment.service.AppointmentService;
import com.blubank.doctorappointment.service.DoctorDTO;
import com.blubank.doctorappointment.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    ResponseEntity<Page<DoctorDTO>> getAllDoctors(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(doctorService.getAllDoctors(page, size));
    }

    @GetMapping(path = "/{doctor_id}")
    ResponseEntity<DoctorDTO> getDoctorById(@PathVariable("doctor_id") Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PostMapping
    ResponseEntity<DoctorDTO> saveDoctor(@Valid @RequestBody @NotNull Doctor newDoctor) {
        return ResponseEntity.ok(doctorService.saveDoctor(newDoctor));
    }

    @DeleteMapping(path = "/{doctor_id}")
    ResponseEntity<String> deleteDoctorById(@PathVariable("doctor_id") Long id) {
        return ResponseEntity.ok(doctorService.deleteDoctorById(id));
    }

    @PostMapping("/add-appointments")
    ResponseEntity<String> saveAppointments(@Valid @RequestParam @NotNull @NotBlank Long doctorId,
                                            @Valid @RequestParam @NotNull @NotBlank @Future LocalDateTime startTime,
                                            @Valid @RequestParam @NotNull @NotBlank @Future LocalDateTime endTime) {
        return ResponseEntity.ok(appointmentService.saveAppointments(doctorId, startTime, endTime));
    }

    @DeleteMapping(path = "/{appointment_id}")
    ResponseEntity<String> deleteAppointmentById(@PathVariable("appointment_id") Long id) {
        return ResponseEntity.ok(appointmentService.deleteAppointmentById(id));
    }

    @GetMapping
    ResponseEntity<Page<AppointmentDTO>> getAllAppointments(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appointmentService.getAllAppointments(page, size));
    }
}
