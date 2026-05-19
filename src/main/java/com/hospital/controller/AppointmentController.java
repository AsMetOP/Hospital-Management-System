package com.hospital.controller;

import com.hospital.model.Appointment;
import com.hospital.model.Appointment.AppointmentStatus;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appointments")
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService,
                                  PatientService patientService,
                                  DoctorService doctorService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @GetMapping
    public String listAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("statuses", AppointmentStatus.values());
        return "appointment/list";
    }

    @GetMapping("/new")
    public String showBookingForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAvailableDoctors());
        model.addAttribute("pageTitle", "Book Appointment");
        return "appointment/form";
    }

    @PostMapping
    public String bookAppointment(@Valid @ModelAttribute("appointment") Appointment appointment,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("doctors", doctorService.getAvailableDoctors());
            model.addAttribute("pageTitle", "Book Appointment");
            return "appointment/form";
        }

        // Manually fetch Patient and Doctor from IDs submitted by form
        try {
            Long patientId = appointment.getPatient().getId();
            Long doctorId  = appointment.getDoctor().getId();
            appointment.setPatient(patientService.getPatientById(patientId));
            appointment.setDoctor(doctorService.getDoctorById(doctorId));

            appointmentService.bookAppointment(appointment);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment booked successfully!");
            return "redirect:/appointments";
        } catch (Exception e) {
            log.error("Error booking appointment: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("doctors", doctorService.getAvailableDoctors());
            return "appointment/form";
        }
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                                @RequestParam AppointmentStatus status,
                                RedirectAttributes redirectAttributes) {
        appointmentService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment status updated to " + status);
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.cancelAppointment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled.");
        return "redirect:/appointments";
    }
}
