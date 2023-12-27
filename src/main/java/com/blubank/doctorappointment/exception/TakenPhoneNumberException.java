package com.blubank.doctorappointment.exception;

public class TakenPhoneNumberException extends  RuntimeException{
    public TakenPhoneNumberException() {
        super("Phone number is taken!");
    }
}
