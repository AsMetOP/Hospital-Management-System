package com.hospital.service.impl;

import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Patient;
import com.hospital.repository.PatientRepository;
import com.hospital.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * PATIENT SERVICE IMPLEMENTATION (BUSINESS LOGIC LAYER)
 *
 * @Service:
 *   Marks this class as a Spring-managed service bean.
 *   When Spring sees @Service, it creates a singleton instance and puts it in the
 *   ApplicationContext (IoC container). Controllers get this injected automatically.
 *
 * @Slf4j (Lombok):
 *   Auto-generates:  private static final Logger log = LoggerFactory.getLogger(PatientServiceImpl.class);
 *   You then use:    log.info("message"), log.error("message"), log.debug("message")
 *   JD REQUIREMENT: "implement Logging ... in web application"
 *
 * @Transactional:
 *   Wraps the method in a database transaction.
 *   If any exception occurs mid-method, ALL database changes are ROLLED BACK.
 *   Example: if savePatient() fails halfway, no partial row is saved.
 *   readOnly = true on read methods → performance optimization (no dirty checking).
 *
 * DEPENDENCY INJECTION via @Autowired:
 *   Spring automatically injects the PatientRepository bean.
 *   This is CONSTRUCTOR INJECTION (preferred over field injection).
 *   Constructor injection makes dependencies explicit and enables easier testing.
 *
 * LAYERED ARCHITECTURE:
 *   Controller → Service → Repository → Database
 *   Each layer has a single responsibility (SRP from SOLID principles).
 */
@Service
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    /**
     * CONSTRUCTOR INJECTION (preferred pattern)
     * When there's only one constructor, @Autowired is optional (Spring injects automatically).
     * Still shown here explicitly for clarity.
     */
    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * SAVE PATIENT (CREATE)
     *
     * @Transactional: if the save fails, nothing is committed.
     * Business rule: check if email already exists before saving.
     *
     * log.info() → INFO level: normal operations you want to track
     * log.error() → ERROR level: exceptions and failures
     * log.debug() → DEBUG level: detailed info for debugging (disabled in production)
     */
    @Override
    @Transactional
    public Patient savePatient(Patient patient) {
        log.info("Attempting to save patient with email: {}", patient.getEmail());

        if (patientRepository.existsByEmail(patient.getEmail())) {
            log.warn("Email already in use: {}", patient.getEmail());
            throw new IllegalArgumentException("A patient with email '" + patient.getEmail() + "' already exists.");
        }

        Patient savedPatient = patientRepository.save(patient);
        log.info("Successfully saved patient with ID: {}", savedPatient.getId());
        return savedPatient;
    }

    /**
     * GET ALL PATIENTS (READ)
     * readOnly = true: tells JPA not to track changes (faster queries).
     */
    @Override
    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        log.debug("Fetching all patients from database");
        List<Patient> patients = patientRepository.findAll();
        log.info("Found {} patients in database", patients.size());
        return patients;
    }

    /**
     * GET PATIENT BY ID (READ)
     *
     * findById() returns Optional<Patient> — it might not exist.
     * .orElseThrow() — if empty, throw our custom exception.
     * This gives a 404 response instead of a 500 server error.
     */
    @Override
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        log.debug("Fetching patient with ID: {}", id);
        return patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Patient not found with ID: {}", id);
                    return new ResourceNotFoundException("Patient", "id", id);
                });
    }

    /**
     * SEARCH PATIENTS
     * Delegates to the repository's LIKE query.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Patient> searchPatients(String keyword) {
        log.info("Searching patients with keyword: '{}'", keyword);
        return patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                keyword, keyword);
    }

    /**
     * UPDATE PATIENT (UPDATE)
     *
     * PATTERN: Fetch existing → update fields → save back
     * Why not just save the incoming patient object?
     *   Because the incoming object from the form might be missing fields
     *   (like createdAt, appointments list). Fetching first preserves them.
     */
    @Override
    @Transactional
    public Patient updatePatient(Long id, Patient patientDetails) {
        log.info("Updating patient with ID: {}", id);

        Patient existingPatient = getPatientById(id); // throws 404 if not found

        // Update only the fields that come from the edit form
        existingPatient.setFirstName(patientDetails.getFirstName());
        existingPatient.setLastName(patientDetails.getLastName());
        existingPatient.setEmail(patientDetails.getEmail());
        existingPatient.setPhone(patientDetails.getPhone());
        existingPatient.setDateOfBirth(patientDetails.getDateOfBirth());
        existingPatient.setGender(patientDetails.getGender());
        existingPatient.setBloodGroup(patientDetails.getBloodGroup());
        existingPatient.setAddress(patientDetails.getAddress());

        Patient updatedPatient = patientRepository.save(existingPatient);
        log.info("Successfully updated patient with ID: {}", updatedPatient.getId());
        return updatedPatient;
    }

    /**
     * DELETE PATIENT (DELETE)
     * Check existence first → throw meaningful error if not found → then delete.
     */
    @Override
    @Transactional
    public void deletePatient(Long id) {
        log.info("Deleting patient with ID: {}", id);
        Patient patient = getPatientById(id); // throws 404 if not found
        patientRepository.delete(patient);
        log.info("Successfully deleted patient with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAlreadyInUse(String email) {
        return patientRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalPatientCount() {
        return patientRepository.count();
    }
}
