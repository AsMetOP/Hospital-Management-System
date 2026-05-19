package com.hospital.service;

import com.hospital.model.Appointment;
import com.hospital.model.Appointment.AppointmentStatus;
import java.util.List;

public interface AppointmentService {
    Appointment bookAppointment(Appointment appointment);
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Long id);
    List<Appointment> getAppointmentsByPatient(Long patientId);
    List<Appointment> getAppointmentsByDoctor(Long doctorId);
    List<Appointment> getTodaysAppointments();
    Appointment updateStatus(Long id, AppointmentStatus status);
    void cancelAppointment(Long id);
    long countByStatus(AppointmentStatus status);
    long getTotalAppointmentCount();
}
