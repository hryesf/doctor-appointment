package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.entity.Doctor;
import com.blubank.doctorappointment.exception.DuplicateDoctorException;
import com.blubank.doctorappointment.exception.TakenMedicalCodeException;
import com.blubank.doctorappointment.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private DoctorConverter doctorConverter;
    @InjectMocks
    private DoctorService underTest;

    @BeforeEach
    void setUp() {
        underTest = new DoctorService(doctorRepository, doctorConverter);
    }

    @Test
    void itShouldGetAllDoctors() {
        // Given
        int size = 10;
        Pageable pageable = Pageable.ofSize(size);
        // When
        underTest.getAllDoctors(size);
        // Then
        verify(doctorRepository).findAll(pageable);
    }

    @Test
    @Disabled
    void itShouldGetDoctorById() {
        // Given
        // When
        // Then
    }

    @Test
    void itShouldSaveDoctor() {
        // Given
        Doctor doctor = new Doctor("John King", "783642734");

        // When
        underTest.saveDoctor(doctor);

        // Then
        ArgumentCaptor<Doctor> doctorArgumentCaptor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository).save(doctorArgumentCaptor.capture());

        Doctor capturedDoctor = doctorArgumentCaptor.getValue();
        assertThat(capturedDoctor).isEqualTo(doctor);
    }

    @Test
    void itWillThrowWhenMedicalCodeIsTaken() {
        // Given
        Doctor doctor = new Doctor("John King", "783642734");

        given(doctorRepository.existsByMedicalCode(doctor.getMedicalCode()))
                .willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> doctorConverter.toEntity(underTest.saveDoctor(doctor)))
                .isInstanceOf(TakenMedicalCodeException.class)
                .hasMessageContaining("Medical code is taken!");

        verify(doctorRepository, never()).save(any());
    }

    @Test
    void itWillThrowWhenMedicalCodeAndFullNameIsTaken() {
        // Given
        Doctor doctor = new Doctor("John King", "783642734");

        given(doctorRepository.existsByMedicalCode(doctor.getMedicalCode()))
                .willReturn(true);
        given(doctorRepository.existsByFullName(doctor.getFullName()))
                .willReturn(true);
        // When
        // Then
        assertThatThrownBy(() -> doctorConverter.toEntity(underTest.saveDoctor(doctor)))
                .isInstanceOf(DuplicateDoctorException.class)
                .hasMessageContaining("Doctor with name \"" + doctor.getFullName() + "\" and medical code \"" + doctor.getMedicalCode() + "\" already exists!");

        verify(doctorRepository, never()).save(any());
    }

    @Test
    void itShouldDeleteDoctorById() {
        // Given
        // When
        // Then
    }
}