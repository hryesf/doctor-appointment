package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.exception.DuplicateDoctorException;
import com.blubank.doctorappointment.exception.NotFoundException;
import com.blubank.doctorappointment.exception.TakenMedicalCodeException;
import com.blubank.doctorappointment.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/v1/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    ResponseEntity<Page<Doctor>> getAllMembers(@RequestParam int page, @RequestParam int size) {
        return new ResponseEntity<>(doctorService.getAllDoctors(page, size), HttpStatus.OK);
    }

    @GetMapping(path = "/{doctor_id}")
    ResponseEntity<Doctor> getDoctor(@PathVariable("doctor_id") Long id) {
        try {
            return new ResponseEntity<>(doctorService.getDoctorById(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PostMapping
    ResponseEntity<Doctor> saveDoctor(@Valid @RequestBody Doctor newDoctor) {
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
}
