package com.blubank.doctorappointment.exception;

public class TakenMedicalCodeException extends  RuntimeException{
    public TakenMedicalCodeException() {
        super("Medical code is taken!");
    }
}
