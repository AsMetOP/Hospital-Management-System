-- ══════════════════════════════════════════════════════════════
-- HOSPITAL MANAGEMENT SYSTEM — DATABASE SCHEMA
-- File: src/main/resources/schema.sql
--
-- Run this manually in MySQL Workbench or MySQL CLI before starting the app.
-- Alternatively, Spring Boot auto-creates tables via JPA (ddl-auto=update).
--
-- JD REQUIREMENT:
--   "Interpret the entities and relationships and create tables,
--    describe relationships between tables and write queries
--    for CRUD operations in MySQL database"
-- ══════════════════════════════════════════════════════════════

-- Create and use the database
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- ── TABLE: patients ──────────────────────────────────────────
-- Stores patient personal information
CREATE TABLE IF NOT EXISTS patients (
    id            BIGINT         AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50)    NOT NULL,
    last_name     VARCHAR(50)    NOT NULL,
    email         VARCHAR(100)   NOT NULL UNIQUE,
    phone         VARCHAR(15)    NOT NULL,
    date_of_birth DATE,
    gender        VARCHAR(10),
    blood_group   VARCHAR(5),
    address       VARCHAR(255),
    created_at    DATETIME       DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- CONSTRAINT: email must be unique across all patients
    CONSTRAINT uq_patient_email UNIQUE (email)
);

-- ── TABLE: doctors ───────────────────────────────────────────
-- Stores doctor profiles and availability
CREATE TABLE IF NOT EXISTS doctors (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    first_name       VARCHAR(50)    NOT NULL,
    last_name        VARCHAR(50)    NOT NULL,
    email            VARCHAR(100)   NOT NULL UNIQUE,
    phone            VARCHAR(15),
    specialization   VARCHAR(100)   NOT NULL,
    qualification    VARCHAR(100),
    experience       INT            DEFAULT 0,
    available        TINYINT(1)     DEFAULT 1,   -- 1 = true, 0 = false
    created_at       DATETIME       DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_doctor_email UNIQUE (email),
    CONSTRAINT chk_experience CHECK (experience >= 0 AND experience <= 50)
);

-- ── TABLE: appointments ──────────────────────────────────────
-- Junction table linking patients and doctors
-- Represents the MANY-TO-MANY relationship simplified as:
--   One Patient → Many Appointments
--   One Doctor  → Many Appointments
CREATE TABLE IF NOT EXISTS appointments (
    id                BIGINT      AUTO_INCREMENT PRIMARY KEY,
    patient_id        BIGINT      NOT NULL,
    doctor_id         BIGINT      NOT NULL,
    appointment_date  DATE        NOT NULL,
    status            VARCHAR(20) DEFAULT 'SCHEDULED',
    notes             TEXT,
    created_at        DATETIME    DEFAULT CURRENT_TIMESTAMP,

    -- FOREIGN KEY CONSTRAINTS — enforce referential integrity
    -- If patient is deleted → delete their appointments (CASCADE)
    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,

    -- If doctor is deleted → delete their appointments (CASCADE)
    CONSTRAINT fk_appointment_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,

    -- ENUM-style constraint on status column
    CONSTRAINT chk_apt_status
        CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED'))
);

-- ══════════════════════════════════════════════════════════════
-- SAMPLE DATA (for testing)
-- ══════════════════════════════════════════════════════════════

-- Insert sample doctors
INSERT IGNORE INTO doctors (first_name, last_name, email, phone, specialization, qualification, experience, available)
VALUES
('Anita',   'Mehta',   'anita.mehta@hospital.com',   '9876540001', 'Cardiology',   'MD, DM (Cardiology)',    12, 1),
('Rajesh',  'Kumar',   'rajesh.kumar@hospital.com',  '9876540002', 'Neurology',    'MBBS, MD (Neurology)',   8,  1),
('Priya',   'Nair',    'priya.nair@hospital.com',    '9876540003', 'Pediatrics',   'MBBS, DCH',              5,  1),
('Vikram',  'Singh',   'vikram.singh@hospital.com',  '9876540004', 'Orthopedics',  'MBBS, MS (Ortho)',       15, 1),
('Sunita',  'Reddy',   'sunita.reddy@hospital.com',  '9876540005', 'Dermatology',  'MBBS, MD (Derma)',       7,  1);

-- Insert sample patients
INSERT IGNORE INTO patients (first_name, last_name, email, phone, gender, blood_group, address)
VALUES
('Arun',    'Sharma',  'arun.sharma@email.com',   '9000000001', 'Male',   'O+',  'Delhi, India'),
('Meena',   'Patel',   'meena.patel@email.com',   '9000000002', 'Female', 'A+',  'Mumbai, India'),
('Suresh',  'Verma',   'suresh.verma@email.com',  '9000000003', 'Male',   'B+',  'Bangalore, India');

-- ══════════════════════════════════════════════════════════════
-- CRUD QUERY EXAMPLES
-- JD REQUIREMENT: "write queries for CRUD operations in MySQL"
-- ══════════════════════════════════════════════════════════════

-- CREATE (INSERT)
-- INSERT INTO patients (first_name, last_name, email, phone) VALUES ('John', 'Doe', 'john@email.com', '9999999999');

-- READ (SELECT with JOIN — shows patient + doctor name in one query)
-- SELECT
--     a.id, p.first_name AS patient, d.first_name AS doctor,
--     d.specialization, a.appointment_date, a.status
-- FROM appointments a
-- JOIN patients p ON a.patient_id = p.id
-- JOIN doctors  d ON a.doctor_id  = d.id
-- WHERE a.appointment_date = CURDATE()
-- ORDER BY a.created_at DESC;

-- UPDATE (change appointment status)
-- UPDATE appointments SET status = 'COMPLETED' WHERE id = 1;

-- DELETE (with FK cascade — also deletes appointments)
-- DELETE FROM patients WHERE id = 1;

-- AGGREGATE — dashboard stats
-- SELECT
--     (SELECT COUNT(*) FROM patients) AS total_patients,
--     (SELECT COUNT(*) FROM doctors WHERE available = 1) AS available_doctors,
--     (SELECT COUNT(*) FROM appointments WHERE status = 'SCHEDULED') AS scheduled_appointments;
