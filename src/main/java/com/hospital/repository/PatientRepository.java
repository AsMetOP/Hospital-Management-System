package com.hospital.repository;

import com.hospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PATIENT REPOSITORY (DATA ACCESS LAYER)
 *
 * WHAT IS A REPOSITORY?
 * The Repository pattern encapsulates all database operations for an entity.
 * Controllers and Services never write SQL directly — they go through repositories.
 *
 * JpaRepository<Patient, Long>:
 *   - Patient = the entity type this repository manages
 *   - Long    = the type of the primary key (@Id field)
 *   - Gives you FREE CRUD methods without writing any code:
 *       save(patient)           → INSERT or UPDATE
 *       findById(id)            → SELECT WHERE id = ?
 *       findAll()               → SELECT * FROM patients
 *       deleteById(id)          → DELETE WHERE id = ?
 *       count()                 → SELECT COUNT(*)
 *       existsById(id)          → SELECT 1 WHERE id = ?
 *
 * HOW DOES JPA KNOW THE SQL?
 *   At startup, Spring Data JPA scans method names and generates SQL:
 *   findByEmail → SELECT * FROM patients WHERE email = ?
 *   findByFirstNameContainingIgnoreCase → SELECT * FROM patients WHERE LOWER(first_name) LIKE ?
 *
 * @Repository:
 *   Marks this as a data access bean.
 *   Spring also translates database exceptions into Spring's DataAccessException hierarchy.
 *
 * JD REQUIREMENT: "write queries for CRUD operations in MySQL database"
 * JD REQUIREMENT: "Use JDBC to access DB" (JPA uses JDBC internally)
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * METHOD NAME QUERY (auto-generated SQL)
     * Spring parses method name: findBy + Email
     * Generated SQL: SELECT * FROM patients WHERE email = ?
     *
     * Returns Optional<Patient> — forces the caller to handle the "not found" case.
     * Better than returning null (avoids NullPointerException).
     */
    Optional<Patient> findByEmail(String email);

    /**
     * findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase
     * Generated SQL:
     *   SELECT * FROM patients
     *   WHERE LOWER(first_name) LIKE LOWER('%?%')
     *      OR LOWER(last_name)  LIKE LOWER('%?%')
     *
     * Used for the search/filter functionality on the patient list page.
     */
    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    /**
     * CUSTOM JPQL QUERY
     * When method names get too long/complex, use @Query with JPQL.
     * JPQL uses class names (Patient) and field names (bloodGroup), NOT table/column names.
     *
     * JD REQUIREMENT: "write queries" — shows you can write custom queries too.
     */
    @Query("SELECT p FROM Patient p WHERE p.bloodGroup = :bloodGroup ORDER BY p.firstName")
    List<Patient> findByBloodGroup(@Param("bloodGroup") String bloodGroup);

    /**
     * NATIVE SQL QUERY
     * nativeQuery = true → write actual MySQL SQL instead of JPQL.
     * Use when JPQL can't express what you need (complex joins, MySQL-specific functions).
     */
    @Query(value = "SELECT COUNT(*) FROM patients WHERE gender = :gender", nativeQuery = true)
    Long countByGender(@Param("gender") String gender);

    /**
     * EXISTS CHECK
     * Used to validate unique email before saving.
     */
    boolean existsByEmail(String email);
}
