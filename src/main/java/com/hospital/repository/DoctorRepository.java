package com.hospital.repository;

import com.hospital.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DOCTOR REPOSITORY
 * Extends JpaRepository — same pattern as PatientRepository.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByEmail(String email);

    boolean existsByEmail(String email);

    // Find all available doctors — for appointment booking dropdown
    List<Doctor> findByAvailableTrue();

    // Find by specialization (case-insensitive search)
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);

    // Count available doctors — used in dashboard stats
    long countByAvailableTrue();

    // Custom JPQL — doctors sorted by years of experience
    @Query("SELECT d FROM Doctor d ORDER BY d.experience DESC")
    List<Doctor> findAllOrderedByExperience();
}
