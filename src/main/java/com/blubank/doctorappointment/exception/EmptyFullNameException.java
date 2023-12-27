package com.blubank.doctorappointment.exception;

public class EmptyFullNameException extends RuntimeException {
    public EmptyFullNameException() {
        super("Full name can not be empty!");
    }
}
