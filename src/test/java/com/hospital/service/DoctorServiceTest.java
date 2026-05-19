package com.hospital.service;

import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;
import com.hospital.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorService Unit Tests")
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor sampleDoctor;

    @BeforeEach
    void setUp() {
        sampleDoctor = Doctor.builder()
                .id(1L)
                .firstName("Anita")
                .lastName("Mehta")
                .email("anita.mehta@hospital.com")
                .specialization("Cardiology")
                .qualification("MD")
                .experience(10)
                .available(true)
                .build();
    }

    @Test
    @DisplayName("Should save doctor when email is not in use")
    void saveDoctor_WhenEmailNew_ShouldSave() {
        given(doctorRepository.existsByEmail(anyString())).willReturn(false);
        given(doctorRepository.save(any(Doctor.class))).willReturn(sampleDoctor);

        Doctor result = doctorService.saveDoctor(sampleDoctor);

        assertThat(result).isNotNull();
        assertThat(result.getSpecialization()).isEqualTo("Cardiology");
        then(doctorRepository).should(times(1)).save(sampleDoctor);
    }

    @Test
    @DisplayName("Should throw exception when doctor email already exists")
    void saveDoctor_WhenEmailExists_ShouldThrow() {
        given(doctorRepository.existsByEmail(anyString())).willReturn(true);

        assertThatThrownBy(() -> doctorService.saveDoctor(sampleDoctor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        then(doctorRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("Should return only available doctors")
    void getAvailableDoctors_ShouldReturnOnlyAvailable() {
        Doctor unavailable = Doctor.builder().id(2L).firstName("Old").lastName("Doc")
                .email("old@doc.com").specialization("X").available(false).build();

        given(doctorRepository.findByAvailableTrue()).willReturn(List.of(sampleDoctor));

        List<Doctor> result = doctorService.getAvailableDoctors();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid doctor ID")
    void getDoctorById_WhenNotFound_ShouldThrow() {
        given(doctorRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.getDoctorById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Doctor");
    }

    @Test
    @DisplayName("Should return correct count of available doctors")
    void getAvailableDoctorCount_ShouldReturnCount() {
        given(doctorRepository.countByAvailableTrue()).willReturn(5L);

        long count = doctorService.getAvailableDoctorCount();

        assertThat(count).isEqualTo(5L);
    }
}
