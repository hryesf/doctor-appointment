package com.blubank.doctorappointment.exception;

public class InvalidStartAndEndTimeException extends RuntimeException {
    public InvalidStartAndEndTimeException() {
        super("The end time cannot be before the start time");
    }
}
