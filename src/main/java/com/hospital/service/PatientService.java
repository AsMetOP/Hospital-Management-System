package com.hospital.service;

import com.hospital.model.Patient;

import java.util.List;

/**
 * PATIENT SERVICE INTERFACE (SERVICE LAYER)
 *
 * WHY DEFINE AN INTERFACE FOR THE SERVICE?
 *
 * 1. PROGRAMMING TO AN ABSTRACTION (OOP principle)
 *    Controllers depend on PatientService (interface), not PatientServiceImpl (class).
 *    If tomorrow you change the implementation (e.g., switch from MySQL to MongoDB),
 *    the controller code doesn't change at all.
 *
 * 2. TESTABILITY
 *    In unit tests, you can mock PatientService using Mockito without any database.
 *    @Mock PatientService patientService; — Mockito creates a fake implementation.
 *
 * 3. SPRING AOP (Aspect-Oriented Programming)
 *    Spring wraps service implementations in a proxy (for transactions, logging, security).
 *    Proxying works cleanest with interfaces.
 *
 * JD REQUIREMENT: "Demonstrate sound knowledge ... in Object Oriented Programming"
 * JD REQUIREMENT: "Apply best design principles" (Interface Segregation, Dependency Inversion)
 *
 * INTERVIEW TIP: "I use the Interface-Implementation pattern in the service layer
 * because it decouples the contract (what the service does) from the implementation
 * (how it does it), making the code testable and extensible."
 */
public interface PatientService {

    // CREATE
    Patient savePatient(Patient patient);

    // READ
    List<Patient> getAllPatients();
    Patient getPatientById(Long id);
    List<Patient> searchPatients(String keyword);

    // UPDATE
    Patient updatePatient(Long id, Patient patientDetails);

    // DELETE
    void deletePatient(Long id);

    // UTILITY
    boolean isEmailAlreadyInUse(String email);
    long getTotalPatientCount();
}
