package com.hospital.service;

import com.hospital.model.Doctor;
import java.util.List;

public interface DoctorService {
    Doctor saveDoctor(Doctor doctor);
    List<Doctor> getAllDoctors();
    Doctor getDoctorById(Long id);
    List<Doctor> getAvailableDoctors();
    List<Doctor> searchDoctors(String specialization);
    Doctor updateDoctor(Long id, Doctor doctorDetails);
    void deleteDoctor(Long id);
    long getTotalDoctorCount();
    long getAvailableDoctorCount();
}
