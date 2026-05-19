package com.hospital.controller;

import com.hospital.model.Appointment.AppointmentStatus;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * DASHBOARD CONTROLLER (CONTROLLER LAYER)
 *
 * @Controller:
 *   Marks this class as a Spring MVC controller.
 *   Methods return VIEW NAMES (strings like "dashboard") — Thymeleaf renders them.
 *   (Compare with @RestController which returns JSON directly.)
 *
 * DIFFERENCE BETWEEN @Controller AND @RestController:
 *   @Controller     → returns view name → Thymeleaf renders HTML page
 *   @RestController → returns data (JSON/XML) → no HTML rendering
 *   JD requires BOTH: Spring MVC (@Controller) + Spring REST (@RestController)
 *
 * Model:
 *   A map of key-value pairs passed from controller to the Thymeleaf template.
 *   model.addAttribute("key", value) → in template: ${key}
 *
 * HOW A REQUEST FLOWS:
 *   1. Browser: GET http://localhost:8080/
 *   2. DispatcherServlet (Spring MVC's front controller) receives the request
 *   3. Maps "/" to showDashboard() via @GetMapping
 *   4. showDashboard() calls services, puts data in Model
 *   5. Returns "dashboard" → DispatcherServlet tells Thymeleaf to render templates/dashboard.html
 *   6. Thymeleaf fills in ${totalPatients} etc. and returns complete HTML
 *   7. HTML sent back to browser
 */
@Controller
@Slf4j
public class DashboardController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    @Autowired
    public DashboardController(PatientService patientService,
                                DoctorService doctorService,
                                AppointmentService appointmentService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    /**
     * GET /  and  GET /dashboard
     *
     * @GetMapping("/") maps HTTP GET requests for "/" to this method.
     * The method name doesn't matter — only the @GetMapping path matters.
     */
    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model) {
        log.info("Loading dashboard statistics");

        // Add stats to the Model — Thymeleaf accesses them as ${totalPatients} etc.
        model.addAttribute("totalPatients",     patientService.getTotalPatientCount());
        model.addAttribute("totalDoctors",      doctorService.getTotalDoctorCount());
        model.addAttribute("availableDoctors",  doctorService.getAvailableDoctorCount());
        model.addAttribute("totalAppointments", appointmentService.getTotalAppointmentCount());
        model.addAttribute("scheduledCount",    appointmentService.countByStatus(AppointmentStatus.SCHEDULED));
        model.addAttribute("completedCount",    appointmentService.countByStatus(AppointmentStatus.COMPLETED));
        model.addAttribute("todaysAppointments",appointmentService.getTodaysAppointments());

        // Returns the VIEW NAME — Spring resolves to: src/main/resources/templates/dashboard.html
        return "dashboard";
    }
}
