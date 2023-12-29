package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Appointment;
import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.exception.InvalidStartAndEndTimeException;
import com.blubank.doctorappointment.exception.NotFoundException;
import com.blubank.doctorappointment.exception.TakenAppointmentException;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DoctorConverter doctorConverter;
    private final PatientConverter patientConverter;
    private final AppointmentConverter appointmentConverter;


    public AppointmentService(AppointmentRepository appointmentRepository, DoctorService doctorService, PatientService patientService, PatientConverter patientConverter, DoctorConverter doctorConverter, AppointmentConverter appointmentConverter) {
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.patientConverter = patientConverter;
        this.doctorConverter = doctorConverter;
        this.appointmentConverter = appointmentConverter;
    }

    public Page<AppointmentDTO> getAllAppointments(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return appointmentConverter.AppointmentDTOPaginated(appointmentRepository.findAll(pageRequest));
    }

    public AppointmentDTO getAppointmentByDateTime(LocalDateTime dateTime) {
        return appointmentConverter.toDto(appointmentRepository
                .findAppointmentByAppointmentDateTime(dateTime)
                .orElseThrow(() -> new NotFoundException("Appointment in selected time (" + dateTime + ") not found!")));
    }

    public Page<AppointmentDTO> getAppointmentsByPatientPhoneNumber(String patientPhoneNumber, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return appointmentConverter.AppointmentDTOPaginated(
                appointmentRepository.findAppointmentsByPatientPhoneNumber(patientPhoneNumber, pageRequest));
    }

    public Page<AppointmentDTO> getOpenAppointments(LocalDate date, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return appointmentConverter.AppointmentDTOPaginated(appointmentRepository.findOpenAppointments(date, pageRequest));
    }

    public AppointmentDTO takeOpenAppointment(LocalDateTime dateTime, String phoneNumber) {
        AppointmentDTO appointmentDTO = getAppointmentByDateTime(dateTime);
        Appointment appointment = appointmentConverter.toEntity(appointmentDTO);

        if (appointment.getPatient() == null) {
            PatientDTO patientDTO = patientService.getPatientByPhoneNumber(phoneNumber);
            Patient patient = patientConverter.toEntity(patientDTO);

            appointment.setPatient(patient);
            appointment.setUpdatedAt(LocalDateTime.now());

            return appointmentConverter.toDto(appointmentRepository.save(appointment));
        } else {
            throw new TakenAppointmentException();
        }
    }

    public String deleteAppointmentById(Long id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            if (appointment.getPatient() != null) {
                appointmentRepository.deleteById(id);
                return "Appointment with code " + id + " removed from the list";
            } else {
                throw new TakenAppointmentException();
            }
        } else {
            throw new NotFoundException("Appointment with id = " + id + " not found!");
        }
    }

    //Doctor
    public String saveAppointments(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {

        if (endTime.isBefore(startTime)) {
            throw new InvalidStartAndEndTimeException();
        }
        DoctorDTO doctorDTO = doctorService.getDoctorById(doctorId);
        Doctor doctor = doctorConverter.toEntity(doctorDTO);

        List<Appointment> appointments = generateAppointments(doctor, startTime, endTime);

        appointmentRepository.saveAll(appointments);

        return "new appointment(s) added for date : " + startTime.toLocalDate().toString();

    }

    private List<Appointment> generateAppointments(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) {
        List<Appointment> appointments = new ArrayList<>();

        while (startTime.isBefore(endTime)) {
            LocalDateTime nextTime = startTime.plusMinutes(30);

            if ((Duration.between(startTime, nextTime).toMinutes() == 30) && (nextTime.isBefore(endTime))) {
                appointments.add(new Appointment(doctor, startTime));
            }
            startTime = nextTime;
        }
        return appointments;
    }
}


