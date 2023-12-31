package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Appointment;
import com.blubank.doctorappointment.entity.AppointmentState;
import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.exception.InvalidStartAndEndTimeException;
import com.blubank.doctorappointment.exception.NotFoundException;
import com.blubank.doctorappointment.exception.TakenAppointmentException;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorService doctorService;
    @Mock
    private PatientService patientService;
    @Mock
    private AppointmentConverter appointmentConverter;
    @InjectMocks
    private AppointmentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AppointmentService(appointmentRepository,
                doctorService,
                patientService,
                appointmentConverter);
    }

    // If there are more than one appointment taken by this user, then all should be shown.
    @Test
    void itShouldGetAppointmentsByPatientPhoneNumber() {

        // Given
        String patientPhoneNumber = "123456789";
        int size = 10;
        Pageable pageable = Pageable.ofSize(size);

        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        List<Appointment> appointments = Arrays.asList(appointment1, appointment2);
        Page<Appointment> appointmentPage = new PageImpl<>(appointments);

        when(appointmentRepository.findAppointmentsByPatientPhoneNumber(patientPhoneNumber, pageable))
                .thenReturn(appointmentPage);

        // When
        Page<Appointment> result = underTest.getAppointmentsByPatientPhoneNumber(patientPhoneNumber, size);

        // Then
        assertThat(result).isEqualTo(appointmentPage);

        verify(appointmentRepository).findAppointmentsByPatientPhoneNumber(patientPhoneNumber, pageable);

    }


    //If there is no appointment with this phone number, then an empty list should be shown.
    @Test
    void itShouldGetEmptyAppointmentListByNotAvailablePatientPhoneNumber() {

        // Given
        String phoneNumber = "09123747536";
        int size = 10;
        Pageable pageable = Pageable.ofSize(size);
        Page<Appointment> emptyAppointmentPage = appointmentRepository.findAppointmentsByPatientPhoneNumber(phoneNumber, pageable);

        // When
        Page<Appointment> appointmentPage = underTest.getAppointmentsByPatientPhoneNumber(phoneNumber, size);

        // Then
        assertThat(appointmentPage).isEqualTo(emptyAppointmentPage);
    }


    // Concurrency check; patient is taking an appointment that is in the process of deletion or being taken by another patient.
    @Test
    void takeOpenAppointment_ConcurrentUpdate() {
        // Given
        Long appointmentId = 1L;
        String phoneNumber = "123456789";

        Appointment appointment = new Appointment();
        appointment.setAppointmentState(AppointmentState.AVAILABLE);

        Patient patient = new Patient();
        patient.setPhoneNumber(phoneNumber);

        // Mock repository response for getting appointment
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // Mock patient service response
        when(patientService.getPatientByPhoneNumber(phoneNumber)).thenReturn(patient);

        // Mock repository behavior to simulate concurrent update
        doThrow(OptimisticLockingFailureException.class)
                .when(appointmentRepository.save(appointment));

        // When and Then
        assertThatThrownBy(() -> underTest.takeOpenAppointment(appointmentId, phoneNumber))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessage("Optimistic locking failure for taking an appointment with id: " + appointmentId);

        // Verify interactions
        verify(appointmentRepository).findById(appointmentId);
        verify(patientService).getPatientByPhoneNumber(phoneNumber);
        verify(appointmentRepository).save(appointment);
    }


    // If either phone number is not given, then an appropriate error message should be given.
    @Test
    void itWillThrowWhenAppointmentIdIsNotAvailable() {
        // Given
        Long appointmentId = 1L;
        String phoneNumber = null;

        // When and Then
        assertThatThrownBy(() -> underTest.takeOpenAppointment(appointmentId, phoneNumber))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Appointment with id = " + appointmentId + " not found!");

    }

    @Test
    void itWillThrowWhenPatientPhoneNumberIsNotAvailable() {
        // Given
        Long appointmentId = 1L;
        String phoneNumber = null;

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);

        // When and Then
        assertThatThrownBy(() -> underTest.takeOpenAppointment(appointmentId, phoneNumber))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Patient with phone number \"" + phoneNumber + "\" not Found");

    }

    // deleteAppointmentById
    // If there is no open appointment then 404 error is shown.
    @Test
    void itWillThrowWhenHaveNoOpenAppointment() {
        // Give
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setAppointmentState(AppointmentState.TAKEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // When
        TakenAppointmentException exception = assertThrows(TakenAppointmentException.class,
                () -> underTest.deleteAppointmentById(appointmentId));

        // Then
        assertEquals("Appointment is taken by a patient!", exception.getMessage());

        verify(appointmentRepository, never()).deleteById(appointmentId);
    }

    //Concurrency check; if doctor is deleting the same appointment that a patient is taking at the same time.
    @Test
    void itWillThrowWhenConcurrencyHappened() {
        // Given
        Long appointmentId = 1L;
        Appointment availableAppointment = new Appointment();
        availableAppointment.setAppointmentState(AppointmentState.AVAILABLE);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(availableAppointment));

        doThrow(OptimisticLockingFailureException.class)
                .when(appointmentRepository).deleteById(appointmentId);

        // When
        String result = underTest.deleteAppointmentById(appointmentId);

        // Then
        assertEquals("Appointment with id = 1 can not remove from the list! \nPlease try again later.", result);

        verify(appointmentRepository).deleteById(appointmentId);
    }


    // getOpenAppointmentsDto

    //If the doctor doesn’t have any open appointment that day, then, an empty list should be shown.
    @Test
    void noOpenAppointmentsForDoctorOnGivenDay() {
        // Given
        LocalDateTime date = LocalDateTime.of(2023, 1, 1, 0, 0); // Replace with the desired date
        int size = 10;
        Pageable pageable = Pageable.ofSize(size);

        List<Appointment> emptyOpenAppointments = Collections.emptyList();
        Page<Appointment> emptyOpenAppointmentsPage = new PageImpl<>(emptyOpenAppointments);
        Page<AppointmentDTO> emptyAppointmentDTOPage = appointmentConverter.AppointmentDTOPaginated(emptyOpenAppointmentsPage);


        when(appointmentRepository.findOpenAppointments(eq(date), eq(pageable)))
                .thenReturn(emptyOpenAppointmentsPage);

        // When
        Page<AppointmentDTO> openAppointments = underTest.getOpenAppointmentsDto(date, size);


        // Then
        assertThat(openAppointments).isEqualTo(emptyAppointmentDTOPage);

        // Verify repository method was called
        verify(appointmentRepository).findOpenAppointments(date, pageable);
    }


    // If doctor enters start and end date so that the period is less than 30 minutes then no time should be added.
    @Test
    void noAppointmentShouldBeAdded() {
        // Given
        Doctor doctor = new Doctor("Lj Swift", "7463853653");
        LocalDateTime startTime = LocalDateTime.of(2024, 2, 1, 9, 0, 0);
        LocalDateTime endTime = startTime.plusMinutes(28);

        // When
        Set<Appointment> appointments = underTest.generateAppointments(doctor, startTime, endTime);

        // Then
        assertThat(appointments).isEqualTo(new HashSet<Appointment>());

    }

    // If doctor enters an end date that is sooner than start date, appropriate error should be shown
    @Test
    void itWillThrowWhenEndDateIsBeforeStartDate() {
        // Given
        Doctor doctor = new Doctor("Lj Swift", "7463853653");
        LocalDateTime startTime = LocalDateTime.of(2024, 2, 1, 9, 0, 0);
        LocalDateTime endTime = startTime.minusHours(1);


        // When
        // Then
        assertThatThrownBy(() -> underTest.saveAppointments(doctor, startTime, endTime))
                .isInstanceOf(InvalidStartAndEndTimeException.class)
                .hasMessage("The end time cannot be before the start time");

    }

    // If there is no appointment set, empty list should be shown.
    @Test
    void noAppointmentShouldBeShown() {

        // Given
        Long doctorId = 1L;
        int size = 10;
        Pageable pageable = Pageable.ofSize(size);
        Page<Appointment> emptyAppointmentPage = appointmentRepository.findAppointmentsByDoctor_Id(doctorId, pageable);

        // When
        Page<Appointment> appointmentPage = underTest.getAppointmentsByDoctorId(doctorId, size);

        // Then
        assertThat(appointmentPage).isEqualTo(emptyAppointmentPage);


    }
}