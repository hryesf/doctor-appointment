package com.blubank.doctorappointment.exception;

public class EmptyPhoneNumberException extends RuntimeException {
    public EmptyPhoneNumberException() {
        super("Phone number can not be empty!");
    }
}
