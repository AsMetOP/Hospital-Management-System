package com.hospital.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DOCTOR ENTITY (MODEL LAYER)
 *
 * DATABASE TABLE GENERATED:
 *   CREATE TABLE doctors (
 *     id BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     first_name VARCHAR(50) NOT NULL,
 *     last_name VARCHAR(50) NOT NULL,
 *     email VARCHAR(100) UNIQUE NOT NULL,
 *     phone VARCHAR(15),
 *     specialization VARCHAR(100) NOT NULL,
 *     qualification VARCHAR(100),
 *     experience INT DEFAULT 0,
 *     available BOOLEAN DEFAULT TRUE,
 *     created_at DATETIME
 *   );
 *
 * RELATIONSHIP:
 *   Doctor → Appointment: One doctor can have many appointments (OneToMany)
 */
@Entity
@Table(name = "doctors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @NotBlank(message = "Specialization is required")
    @Column(name = "specialization", nullable = false, length = 100)
    private String specialization;

    @Column(name = "qualification", length = 100)
    private String qualification;

    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience seems too high")
    @Column(name = "experience")
    private Integer experience;

    /**
     * BOOLEAN FIELD
     * Maps to TINYINT(1) in MySQL — true=1, false=0.
     * columnDefinition sets the default value at DB level.
     */
    @Column(name = "available", columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean available = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    public String getFullName() {
        return "Dr. " + firstName + " " + lastName;
    }
}
