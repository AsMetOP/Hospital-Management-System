package com.hospital.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PATIENT ENTITY (MODEL LAYER)
 *
 * WHAT IS AN ENTITY?
 * A Java class mapped to a database TABLE. Every instance = one row.
 * JPA (Java Persistence API) does the object ↔ row conversion for you.
 *
 * LOMBOK ANNOTATIONS (reduces boilerplate):
 *   @Data          → generates getters, setters, toString, equals, hashCode
 *   @Builder       → Patient.builder().firstName("Raj").build() — clean object creation
 *   @NoArgsConstructor → generates Patient() — needed by JPA
 *   @AllArgsConstructor → generates Patient(id, firstName, ...) — needed by @Builder
 *
 * JPA ANNOTATIONS:
 *   @Entity        → tells JPA: map this class to a database table
 *   @Table         → specify exact table name in MySQL
 *   @Id            → marks the primary key field
 *   @GeneratedValue → AUTO_INCREMENT in MySQL (DB generates the ID)
 *   @Column        → customize column name, nullable, length
 *
 * VALIDATION ANNOTATIONS (@NotBlank etc.):
 *   These trigger when you use @Valid in the controller.
 *   If validation fails, Spring returns the form with error messages.
 *   JD requirement: "implement best coding standards and practices"
 *
 * DATABASE TABLE GENERATED (MySQL DDL equivalent):
 *   CREATE TABLE patients (
 *     id BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     first_name VARCHAR(50) NOT NULL,
 *     last_name VARCHAR(50) NOT NULL,
 *     email VARCHAR(100) UNIQUE NOT NULL,
 *     phone VARCHAR(15) NOT NULL,
 *     date_of_birth DATE,
 *     gender VARCHAR(10),
 *     blood_group VARCHAR(5),
 *     address VARCHAR(255),
 *     created_at DATETIME,
 *     updated_at DATETIME
 *   );
 */
@Entity
@Table(name = "patients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be 2-50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be 2-50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be exactly 10 digits")
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    @Column(name = "address", length = 255)
    private String address;

    /**
     * AUTO-POPULATED AUDIT FIELDS
     * @CreationTimestamp: Hibernate sets this when a new row is inserted
     * @UpdateTimestamp:   Hibernate sets this whenever the row is updated
     * updatable = false: once set, createdAt never changes on UPDATE
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * ONE-TO-MANY RELATIONSHIP
     * One patient can have many appointments.
     * mappedBy = "patient" — the Appointment entity owns the FK column (patient_id).
     * cascade = REMOVE — if patient deleted, their appointments are deleted too.
     * fetch = LAZY — don't load appointments unless explicitly accessed (performance).
     *
     * JD requirement: "Interpret the entities and relationships"
     */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    // HELPER METHOD: returns full name as a single string
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
