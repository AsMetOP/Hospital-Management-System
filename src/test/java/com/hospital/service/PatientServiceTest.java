package com.hospital.service;

import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Patient;
import com.hospital.repository.PatientRepository;
import com.hospital.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * UNIT TESTS FOR PatientServiceImpl
 *
 * JD REQUIREMENT: "Write unit test cases for a given code using JUnit and Mockito"
 *
 * KEY ANNOTATIONS:
 *
 * @ExtendWith(MockitoExtension.class):
 *   Integrates Mockito with JUnit 5.
 *   Initializes @Mock and @InjectMocks automatically before each test.
 *   Without this, you'd have to call MockitoAnnotations.openMocks(this) manually.
 *
 * @Mock:
 *   Creates a FAKE (mock) PatientRepository — no real database involved.
 *   You control what the fake returns using when(...).thenReturn(...).
 *   This is the "M" in Mockito — we mock dependencies.
 *
 * @InjectMocks:
 *   Creates the REAL PatientServiceImpl and injects all @Mock objects into it.
 *   So PatientServiceImpl gets a fake PatientRepository (not a real one).
 *
 * WHY MOCK THE REPOSITORY?
 *   Unit tests should test ONE class in isolation.
 *   If we used a real repository, the test would depend on MySQL being running.
 *   Mocking lets us test the SERVICE LOGIC independently of the database.
 *
 * GIVEN / WHEN / THEN (BDD pattern):
 *   GIVEN: set up the test data and mock behavior
 *   WHEN:  call the method under test
 *   THEN:  assert the expected outcome
 *   This pattern makes tests readable like English sentences.
 *
 * INTERVIEW TIP: "I follow the Arrange-Act-Assert (AAA) pattern, also known as
 * Given-When-Then in BDD. Each test has a single responsibility and tests one
 * specific behavior. I mock the repository layer so service tests are fast
 * and don't require a running database."
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService Unit Tests")
class PatientServiceTest {

    // @Mock creates a fake PatientRepository — no real DB
    @Mock
    private PatientRepository patientRepository;

    // @InjectMocks creates real PatientServiceImpl with fake repository injected
    @InjectMocks
    private PatientServiceImpl patientService;

    // Test data — created fresh before each test
    private Patient samplePatient;

    /**
     * @BeforeEach — runs before EVERY test method.
     * Rebuilds the test data to ensure test isolation.
     * Tests should never depend on each other's state.
     */
    @BeforeEach
    void setUp() {
        samplePatient = Patient.builder()
                .id(1L)
                .firstName("Raj")
                .lastName("Sharma")
                .email("raj.sharma@email.com")
                .phone("9876543210")
                .gender("Male")
                .bloodGroup("O+")
                .build();
    }

    // ════════════════════════════════════════════════════════
    // SAVE PATIENT TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("Should save patient when email is not already in use")
    void savePatient_WhenEmailNotExists_ShouldReturnSavedPatient() {
        // GIVEN — set up mock behavior
        // "When patientRepository.existsByEmail() is called with any String, return false"
        given(patientRepository.existsByEmail(anyString())).willReturn(false);
        // "When patientRepository.save() is called with any Patient, return samplePatient"
        given(patientRepository.save(any(Patient.class))).willReturn(samplePatient);

        // WHEN — call the actual service method
        Patient result = patientService.savePatient(samplePatient);

        // THEN — verify the result and interactions
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("raj.sharma@email.com");
        assertThat(result.getFirstName()).isEqualTo("Raj");

        // Verify repository methods were called exactly once
        // This ensures the service isn't doing something unexpected
        then(patientRepository).should(times(1)).existsByEmail("raj.sharma@email.com");
        then(patientRepository).should(times(1)).save(samplePatient);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email already exists")
    void savePatient_WhenEmailAlreadyExists_ShouldThrowException() {
        // GIVEN — email already in use
        given(patientRepository.existsByEmail(anyString())).willReturn(true);

        // THEN — assertThatThrownBy verifies an exception IS thrown
        assertThatThrownBy(() -> patientService.savePatient(samplePatient))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        // Verify save() was NEVER called (we short-circuited due to duplicate email)
        then(patientRepository).should(never()).save(any(Patient.class));
    }

    // ════════════════════════════════════════════════════════
    // GET ALL PATIENTS TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("Should return list of all patients")
    void getAllPatients_ShouldReturnAllPatients() {
        // GIVEN
        Patient patient2 = Patient.builder()
                .id(2L).firstName("Priya").lastName("Singh")
                .email("priya@email.com").phone("9876543211").build();

        List<Patient> patientList = Arrays.asList(samplePatient, patient2);
        given(patientRepository.findAll()).willReturn(patientList);

        // WHEN
        List<Patient> result = patientService.getAllPatients();

        // THEN
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Raj");
        assertThat(result.get(1).getFirstName()).isEqualTo("Priya");
        then(patientRepository).should(times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no patients exist")
    void getAllPatients_WhenNoPatientsExist_ShouldReturnEmptyList() {
        // GIVEN — repository returns empty list
        given(patientRepository.findAll()).willReturn(List.of());

        // WHEN
        List<Patient> result = patientService.getAllPatients();

        // THEN
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    // ════════════════════════════════════════════════════════
    // GET PATIENT BY ID TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("Should return patient when valid ID is given")
    void getPatientById_WhenPatientExists_ShouldReturnPatient() {
        // GIVEN — wrap patient in Optional (that's what findById returns)
        given(patientRepository.findById(1L)).willReturn(Optional.of(samplePatient));

        // WHEN
        Patient result = patientService.getPatientById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("Raj Sharma");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when patient ID does not exist")
    void getPatientById_WhenPatientNotFound_ShouldThrowResourceNotFoundException() {
        // GIVEN — no patient found for ID 99
        given(patientRepository.findById(99L)).willReturn(Optional.empty());

        // THEN — exception should be thrown with correct details
        assertThatThrownBy(() -> patientService.getPatientById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient")
                .hasMessageContaining("99");
    }

    // ════════════════════════════════════════════════════════
    // UPDATE PATIENT TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("Should update patient details when patient exists")
    void updatePatient_WhenPatientExists_ShouldReturnUpdatedPatient() {
        // GIVEN
        Patient updatedDetails = Patient.builder()
                .firstName("Rajesh")
                .lastName("Sharma")
                .email("rajesh.updated@email.com")
                .phone("9000000000")
                .build();

        Patient savedAfterUpdate = Patient.builder()
                .id(1L)
                .firstName("Rajesh")
                .lastName("Sharma")
                .email("rajesh.updated@email.com")
                .phone("9000000000")
                .build();

        given(patientRepository.findById(1L)).willReturn(Optional.of(samplePatient));
        given(patientRepository.save(any(Patient.class))).willReturn(savedAfterUpdate);

        // WHEN
        Patient result = patientService.updatePatient(1L, updatedDetails);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Rajesh");
        assertThat(result.getEmail()).isEqualTo("rajesh.updated@email.com");
        then(patientRepository).should(times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent patient")
    void updatePatient_WhenPatientNotFound_ShouldThrowException() {
        given(patientRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.updatePatient(999L, samplePatient))
                .isInstanceOf(ResourceNotFoundException.class);

        then(patientRepository).should(never()).save(any());
    }

    // ════════════════════════════════════════════════════════
    // DELETE PATIENT TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("Should delete patient when patient exists")
    void deletePatient_WhenPatientExists_ShouldDeleteSuccessfully() {
        // GIVEN
        given(patientRepository.findById(1L)).willReturn(Optional.of(samplePatient));
        // willDoNothing() for void methods — delete() returns void
        willDoNothing().given(patientRepository).delete(any(Patient.class));

        // WHEN
        patientService.deletePatient(1L);

        // THEN — verify delete was called once
        then(patientRepository).should(times(1)).delete(samplePatient);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent patient")
    void deletePatient_WhenPatientNotFound_ShouldThrowException() {
        given(patientRepository.findById(404L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.deletePatient(404L))
                .isInstanceOf(ResourceNotFoundException.class);

        then(patientRepository).should(never()).delete(any());
    }

    // ════════════════════════════════════════════════════════
    // UTILITY METHOD TESTS
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("Should return correct total patient count")
    void getTotalPatientCount_ShouldReturnCount() {
        given(patientRepository.count()).willReturn(42L);

        long count = patientService.getTotalPatientCount();

        assertThat(count).isEqualTo(42L);
        then(patientRepository).should(times(1)).count();
    }

    @Test
    @DisplayName("Should return true when email is already in use")
    void isEmailAlreadyInUse_WhenEmailExists_ShouldReturnTrue() {
        given(patientRepository.existsByEmail("taken@email.com")).willReturn(true);

        boolean result = patientService.isEmailAlreadyInUse("taken@email.com");

        assertThat(result).isTrue();
    }
}
