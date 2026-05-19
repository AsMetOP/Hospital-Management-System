package com.hospital.repository;

import com.hospital.model.Appointment;
import com.hospital.model.Appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * APPOINTMENT REPOSITORY
 *
 * Notice the JOIN queries — appointments always need patient and doctor data.
 * JD REQUIREMENT: "describe relationships between tables and write queries"
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Find all appointments for a specific patient
    List<Appointment> findByPatientId(Long patientId);

    // Find all appointments for a specific doctor
    List<Appointment> findByDoctorId(Long doctorId);

    // Filter by status
    List<Appointment> findByStatus(AppointmentStatus status);

    // Count by status — used in dashboard stats
    long countByStatus(AppointmentStatus status);

    // Find appointments on a specific date
    List<Appointment> findByAppointmentDate(LocalDate date);

    // Today's appointments — pass LocalDate.now() from service
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :today ORDER BY a.doctor.firstName")
    List<Appointment> findTodaysAppointments(@Param("today") LocalDate today);

    // Check if a doctor already has an appointment on that date (to avoid double-booking)
    boolean existsByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
}
