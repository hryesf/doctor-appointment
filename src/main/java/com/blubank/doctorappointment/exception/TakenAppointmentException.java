package com.blubank.doctorappointment.exception;

public class TakenAppointmentException extends  RuntimeException{
    public TakenAppointmentException() {
        super("Appointment is taken by a patient!");
    }
}
