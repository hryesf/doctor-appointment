package com.blubank.doctorappointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(DuplicateDoctorException.class)
    public ResponseEntity<String> handleDuplicateDoctorException(DuplicateDoctorException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(TakenMedicalCodeException.class)
    public ResponseEntity<String> handleTakenMedicalCodeException(TakenMedicalCodeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(value = {InvalidStartAndEndTimeException.class})
    public ResponseEntity<String> handleInvalidStartAndEndTimeException(InvalidStartAndEndTimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


    @ExceptionHandler(value = {DuplicatePatientException.class})
    public ResponseEntity<String> handleDuplicatePatientException(DuplicatePatientException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(value = {TakenAppointmentException.class})
    public ResponseEntity<String> handleTakenAppointmentException(TakenAppointmentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(value = {TakenPhoneNumberException.class})
    public ResponseEntity<String> handleTakenPhoneNumberException(TakenPhoneNumberException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

}
