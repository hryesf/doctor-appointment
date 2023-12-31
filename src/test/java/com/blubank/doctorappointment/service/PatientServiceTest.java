package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Patient;
import com.blubank.doctorappointment.exception.DuplicatePatientException;
import com.blubank.doctorappointment.exception.TakenPhoneNumberException;
import com.blubank.doctorappointment.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PatientConverter patientConverter;
    @InjectMocks
    private PatientService underTest;
    
    @BeforeEach
    void setUp() {
        underTest = new PatientService(patientRepository, patientConverter);
    }

    @Test
    void itShouldGetAllPatientsDto() {
        // Given
        int size = 10;
        Pageable pageable = Pageable.ofSize(size);
        // When
        underTest.getAllPatientsDto(size);
        // Then
        verify(patientRepository).findAll(pageable);
    }

    @Test
    void itWillThrowWhenPhoneNumberIsTaken() {
        // Given
        Patient patient = new Patient("John King", "783642734");

        given(patientRepository.existsByPhoneNumber(patient.getPhoneNumber()))
                .willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> underTest.savePatient(patient))
                .isInstanceOf(TakenPhoneNumberException.class)
                .hasMessageContaining("Phone number is taken!");

        verify(patientRepository, never()).save(any());
    }

    @Test
    void itWillThrowWhenPhoneNumberAndFullNameIsTaken() {
        // Given
        Patient patient = new Patient("John King", "783642734");

        given(patientRepository.existsByPhoneNumber(patient.getPhoneNumber()))
                .willReturn(true);
        given(patientRepository.existsByFullName(patient.getFullName()))
                .willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> underTest.savePatient(patient))
                .isInstanceOf(DuplicatePatientException.class)
                .hasMessageContaining("Patient with name \"" + patient.getFullName() + "\" and phone number \"" + patient.getPhoneNumber() + "\" is already registered!");

        verify(patientRepository, never()).save(any());
    }

    @Test
    void itShouldSavePatient() {
        // Given
        Patient patient = new Patient("John King", "097364265");

        // When
        underTest.savePatient(patient);

        // Then
        ArgumentCaptor<Patient> PatientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(PatientArgumentCaptor.capture());

        Patient capturedPatient = PatientArgumentCaptor.getValue();
        assertThat(capturedPatient).isEqualTo(patient);
    }
}