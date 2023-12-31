package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Appointment;
import com.blubank.doctorappointment.entity.AppointmentState;
import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.exception.InvalidStartAndEndTimeException;
import com.blubank.doctorappointment.exception.NotFoundException;
import com.blubank.doctorappointment.exception.TakenAppointmentException;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AppointmentService {
    private final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentConverter appointmentConverter;


    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorService doctorService,
                              PatientService patientService,
                              AppointmentConverter appointmentConverter) {
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointmentConverter = appointmentConverter;
    }


    public Page<AppointmentDTO> getAllAppointmentsDto(int size) {
        Pageable pageable = Pageable.ofSize(size);
        return appointmentConverter.AppointmentDTOPaginated(appointmentRepository.findAll(pageable));
    }

    public Page<AppointmentDTO> getAppointmentsDtoByPatientPhoneNumber(String patientPhoneNumber, int size) {
        Pageable pageable = Pageable.ofSize(size);
        return appointmentConverter.AppointmentDTOPaginated(
                appointmentRepository.findAppointmentsByPatientPhoneNumber(patientPhoneNumber, pageable));
    }

    public Page<AppointmentDTO> getAppointmentsDtoByDoctorId(Long doctorId, int size) {
        Pageable pageable = Pageable.ofSize(size);
        return appointmentConverter.AppointmentDTOPaginated(
                appointmentRepository.findAppointmentsByDoctor_Id(doctorId, pageable));
    }

    public Page<Appointment> getAppointmentsByDoctorId(Long doctorId, int size) {
        Pageable pageable = Pageable.ofSize(size);
        return appointmentRepository.findAppointmentsByDoctor_Id(doctorId, pageable);
    }

    public Page<AppointmentDTO> getOpenAppointmentsDto(LocalDateTime date, int size) {
        Pageable pageable = Pageable.ofSize(size);
        return appointmentConverter.AppointmentDTOPaginated(appointmentRepository.findOpenAppointments(date, pageable));
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment with id = " + id + " not found!"));
    }

    public AppointmentDTO takeOpenAppointment(Long appointmentId, String phoneNumber) {
        Appointment appointment = getAppointmentById(appointmentId);
        Patient patient = patientService.getPatientByPhoneNumber(phoneNumber);

        if (appointment.getAppointmentState() == AppointmentState.TAKEN) {
            logger.error("Selected appointment is already taken by another patient.");
            throw new TakenAppointmentException();
        }

        appointment.setPatient(patient);
        appointment.setAppointmentState(AppointmentState.TAKEN);
        appointment.setUpdatedAt(LocalDateTime.now());

        try {
            appointmentRepository.save(appointment);
            logger.info("Taking appointment was successfully processed.");
            return appointmentConverter.toDto(appointment);

        } catch (OptimisticLockingFailureException e) {
            logger.error("Optimistic locking failure for taking an appointment with id: {}", appointmentId);
            throw e;
        }

    }


    public String deleteAppointmentById(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);

        if (appointment.getAppointmentState() == AppointmentState.TAKEN || appointment.getPatient() != null) {
            throw new TakenAppointmentException();
        }

        try {
            appointmentRepository.deleteById(appointmentId);
            logger.info("Deleting appointment was successfully processed.");
            return "Appointment with code " + appointmentId + " removed from the list";

        } catch (OptimisticLockingFailureException e) {
            logger.error("Optimistic locking failure for taking an appointment with id: {}", appointmentId);
            return "Appointment with id = " + appointmentId + " can not remove from the list! \nPlease try again later.";
        }
    }

    public String saveAppointments(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) {

        if (endTime.isBefore(startTime)) {
            throw new InvalidStartAndEndTimeException();
        }

        Set<Appointment> appointments = generateAppointments(doctor, startTime, endTime);

        doctor.setAppointmentList(appointments);
        doctorService.saveDoctor(doctor);

        return appointments.size() + " new appointment(s) added for date : " + startTime.toLocalDate().toString();

    }

    public Set<Appointment> generateAppointments(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) {
        Set<Appointment> appointments = new HashSet<>();

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


