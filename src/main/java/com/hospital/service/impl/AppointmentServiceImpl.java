package com.hospital.service.impl;

import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Appointment;
import com.hospital.model.Appointment.AppointmentStatus;
import com.hospital.repository.AppointmentRepository;
import com.hospital.service.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public Appointment bookAppointment(Appointment appointment) {
        log.info("Booking appointment for patient ID: {} with doctor ID: {}",
                appointment.getPatient().getId(), appointment.getDoctor().getId());

        // Business rule: prevent double-booking same doctor on same date
        boolean alreadyBooked = appointmentRepository.existsByDoctorIdAndAppointmentDate(
                appointment.getDoctor().getId(), appointment.getAppointmentDate());

        if (alreadyBooked) {
            log.warn("Doctor ID {} already has an appointment on {}",
                    appointment.getDoctor().getId(), appointment.getAppointmentDate());
            throw new IllegalStateException("This doctor already has an appointment on the selected date.");
        }

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment booked successfully with ID: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getTodaysAppointments() {
        return appointmentRepository.findTodaysAppointments(LocalDate.now());
    }

    @Override
    @Transactional
    public Appointment updateStatus(Long id, AppointmentStatus status) {
        log.info("Updating appointment {} status to {}", id, status);
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id) {
        log.info("Cancelling appointment with ID: {}", id);
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(AppointmentStatus status) {
        return appointmentRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalAppointmentCount() {
        return appointmentRepository.count();
    }
}
