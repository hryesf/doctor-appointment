package com.blubank.doctorappointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {EmptyFullNameException.class})
    public ResponseEntity<Object> handleEmptyFullNameException(EmptyFullNameException e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {EmptyPhoneNumberException.class})
    public ResponseEntity<Object> handleEmptyPhoneNumberException(EmptyPhoneNumberException e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidStartAndEndTimeException.class})
    public ResponseEntity<Object> handleInvalidStartAndEndTimeException(InvalidStartAndEndTimeException e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {DuplicateDoctorException.class})
    public ResponseEntity<Object> handleDuplicateDoctorException(DuplicateDoctorException e) {
        return buildResponseEntity(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {DuplicatePatientException.class})
    public ResponseEntity<Object> handleDuplicatePatientException(DuplicatePatientException e) {
        return buildResponseEntity(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {TakenAppointmentException.class})
    public ResponseEntity<Object> handleTakenAppointmentException(TakenAppointmentException e) {
        return buildResponseEntity(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {TakenMedicalCodeException.class})
    public ResponseEntity<Object> handleTakenMedicalCodeException(TakenMedicalCodeException e) {
        return buildResponseEntity(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {TakenPhoneNumberException.class})
    public ResponseEntity<Object> handleTakenPhoneNumberException(TakenPhoneNumberException e) {
        return buildResponseEntity(e, HttpStatus.CONFLICT);
    }

    private ResponseEntity<Object> buildResponseEntity(Exception exception, HttpStatus status) {
        APIException apiException = new APIException(exception.getMessage(), status, ZonedDateTime.now());
        return new ResponseEntity<>(apiException, status);
    }

}
