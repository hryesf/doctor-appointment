package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Appointment;
import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.exception.InvalidStartAndEndTimeException;
import com.blubank.doctorappointment.exception.NotFoundException;
import com.blubank.doctorappointment.exception.TakenAppointmentException;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorService doctorService, PatientService patientService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment with id " + id + " not found!"));
    }

    public Appointment getAppointmentByDateTime(LocalDateTime dateTime) {
        return appointmentRepository.findAppointmentByAppointmentDateTime(dateTime)
                .orElseThrow(() -> new NotFoundException("Appointment in selected time (" + dateTime + ") not found!"));
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findAppointmentsByPatientId(patientId);
    }

    public List<Appointment> getOpenAppointments(LocalDate date) {
        return appointmentRepository.findOpenAppointments(date);
    }

    //Patient
    public Appointment takeOpenAppointment(LocalDateTime dateTime, String phoneNumber) {
        Appointment appointment = getAppointmentByDateTime(dateTime);
        if (appointment.getPatient() == null) {
            Patient patient = patientService.getPatientByPhoneNumber(phoneNumber);
            appointment.setPatient(patient);
            return appointmentRepository.save(appointment);
        }else {
            throw new TakenAppointmentException();
        }
    }

    public String deleteAppointmentById(Long id) {

        if ((appointmentRepository.findById(id).isPresent()) && (appointmentRepository.findById(id).get().getPatient() != null)) {
            appointmentRepository.deleteById(id);
            return "appointment with code " + id + " removed from list";

        } else if (appointmentRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Appointment with id = " + id + " not found!");

        } else {
            throw new TakenAppointmentException();
        }
    }

    //Doctor
    public String saveAppointments(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) throws InvalidStartAndEndTimeException {

        if (endTime.isBefore(startTime)) {
            throw new InvalidStartAndEndTimeException();
        } else {
            Doctor doctor = doctorService.getDoctorById(doctorId);

            while (startTime.isBefore(endTime)) {
                LocalDateTime nextTime = startTime.plusMinutes(30);
                if (Duration.between(startTime, nextTime).toMinutes() >= 30) {
                    appointmentRepository.save(new Appointment(doctor, startTime));
                }
                startTime = nextTime;
            }

            return "new appointment(s) added for date : " + startTime.toLocalDate().toString();
        }
    }


}
