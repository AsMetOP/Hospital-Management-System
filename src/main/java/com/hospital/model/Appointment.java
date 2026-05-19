package com.hospital.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * APPOINTMENT ENTITY (MODEL LAYER)
 *
 * This entity demonstrates a MANY-TO-MANY bridging scenario simplified as:
 *   Patient (1) ← → (Many) Appointment (Many) ← → (1) Doctor
 *
 * HOW FOREIGN KEYS WORK HERE:
 *   @ManyToOne — many appointments can belong to one patient/doctor
 *   @JoinColumn — specifies the FK column name in the appointments table
 *
 * RESULTING SQL TABLE:
 *   CREATE TABLE appointments (
 *     id BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     patient_id BIGINT NOT NULL,
 *     doctor_id BIGINT NOT NULL,
 *     appointment_date DATE NOT NULL,
 *     status ENUM('SCHEDULED','COMPLETED','CANCELLED') DEFAULT 'SCHEDULED',
 *     notes TEXT,
 *     created_at DATETIME,
 *     FOREIGN KEY (patient_id) REFERENCES patients(id),
 *     FOREIGN KEY (doctor_id) REFERENCES doctors(id)
 *   );
 *
 * JD REQUIREMENT: "Interpret the entities and relationships and create tables,
 * describe relationships between tables"
 */
@Entity
@Table(name = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * MANY-TO-ONE RELATIONSHIP WITH PATIENT
     *
     * @ManyToOne: Many appointments → One patient
     * fetch = EAGER: load patient data immediately with appointment (needed for display)
     * @JoinColumn: creates patient_id column in appointments table as a FK
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private Patient patient;

    /**
     * MANY-TO-ONE RELATIONSHIP WITH DOCTOR
     * Same pattern as patient above.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    /**
     * @DateTimeFormat: tells Spring MVC how to parse the date from the HTML form input.
     * HTML date inputs submit as "yyyy-MM-dd" string; this annotation converts it to LocalDate.
     */
    @NotNull(message = "Appointment date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    /**
     * ENUM FIELD
     * @Enumerated(STRING) stores the enum name ("SCHEDULED") instead of ordinal (0).
     * Always use STRING — ordinal breaks if you reorder the enum values.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * ENUM: Appointment Status
     *
     * Using an enum (instead of a plain String) enforces valid values at compile time.
     * You can't accidentally set status = "CANCLED" (typo) — it won't compile.
     * JD requirement: "Apply best design principles ... to solve a problem"
     */
    public enum AppointmentStatus {
        SCHEDULED, COMPLETED, CANCELLED
    }
}
