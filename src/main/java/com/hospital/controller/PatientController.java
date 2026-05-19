package com.hospital.controller;

import com.hospital.model.Patient;
import com.hospital.service.PatientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * PATIENT CONTROLLER — FULL CRUD
 *
 * URL STRUCTURE (REST-ful MVC routing):
 *   GET  /patients          → list all patients
 *   GET  /patients/new      → show empty add form
 *   POST /patients          → save new patient (form submission)
 *   GET  /patients/{id}     → view single patient
 *   GET  /patients/{id}/edit → show pre-filled edit form
 *   POST /patients/{id}     → save edits (form submission)
 *   POST /patients/{id}/delete → delete patient
 *
 * @RequestMapping("/patients"):
 *   All methods in this controller have "/patients" as base URL prefix.
 *   Avoids repeating "/patients" in every @GetMapping.
 *
 * @PathVariable:
 *   Extracts value from URL path.
 *   @GetMapping("/{id}") + @PathVariable Long id → extracts 42 from /patients/42
 *
 * @Valid + BindingResult:
 *   @Valid triggers validation annotations on Patient (@NotBlank, @Email etc.)
 *   BindingResult holds validation errors — must be the next parameter after @Valid.
 *   If errors exist → redisplay form. If no errors → save and redirect.
 *
 * RedirectAttributes:
 *   Passes flash messages across a redirect (message survives the redirect, then disappears).
 *   Pattern: POST → redirect → GET (PRG Pattern: Post-Redirect-Get)
 *   Prevents duplicate form submissions on browser refresh.
 *
 * JD REQUIREMENT: "Develop REST web services using Spring REST and Spring Boot"
 *                 "Use MVC components for coding the presentation layer"
 */
@Controller
@RequestMapping("/patients")
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // ══════════════════════════════════════════════════
    // LIST — GET /patients
    // ══════════════════════════════════════════════════

    @GetMapping
    public String listPatients(@RequestParam(required = false) String search, Model model) {
        log.info("Patient list page requested. Search keyword: '{}'", search);

        List<Patient> patients;
        if (search != null && !search.trim().isEmpty()) {
            patients = patientService.searchPatients(search.trim());
            model.addAttribute("search", search);
        } else {
            patients = patientService.getAllPatients();
        }

        model.addAttribute("patients", patients);
        model.addAttribute("totalCount", patients.size());
        return "patient/list";     // → templates/patient/list.html
    }

    // ══════════════════════════════════════════════════
    // SHOW ADD FORM — GET /patients/new
    // ══════════════════════════════════════════════════

    @GetMapping("/new")
    public String showAddForm(Model model) {
        log.info("Showing add patient form");
        // Add an empty Patient object — Thymeleaf binds form fields to it
        model.addAttribute("patient", new Patient());
        model.addAttribute("pageTitle", "Add New Patient");
        return "patient/form";     // → templates/patient/form.html
    }

    // ══════════════════════════════════════════════════
    // SAVE NEW PATIENT — POST /patients
    // ══════════════════════════════════════════════════

    @PostMapping
    public String savePatient(@Valid @ModelAttribute("patient") Patient patient,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        log.info("Form submitted to save patient: {}", patient.getEmail());

        // If validation failed, redisplay the form with error messages
        if (result.hasErrors()) {
            log.warn("Validation errors found: {}", result.getErrorCount());
            model.addAttribute("pageTitle", "Add New Patient");
            return "patient/form";    // stay on form, show errors
        }

        try {
            patientService.savePatient(patient);
            // Flash message: survives one redirect, then gone
            redirectAttributes.addFlashAttribute("successMessage",
                    "Patient '" + patient.getFirstName() + " " + patient.getLastName() + "' added successfully!");
            return "redirect:/patients";   // PRG Pattern: redirect after POST
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Add New Patient");
            return "patient/form";
        }
    }

    // ══════════════════════════════════════════════════
    // VIEW SINGLE PATIENT — GET /patients/{id}
    // ══════════════════════════════════════════════════

    @GetMapping("/{id}")
    public String viewPatient(@PathVariable Long id, Model model) {
        log.info("Viewing patient with ID: {}", id);
        Patient patient = patientService.getPatientById(id); // throws 404 if not found
        model.addAttribute("patient", patient);
        return "patient/view";
    }

    // ══════════════════════════════════════════════════
    // SHOW EDIT FORM — GET /patients/{id}/edit
    // ══════════════════════════════════════════════════

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.info("Showing edit form for patient ID: {}", id);
        Patient patient = patientService.getPatientById(id);
        model.addAttribute("patient", patient);
        model.addAttribute("pageTitle", "Edit Patient");
        return "patient/form";     // reuses the same form template
    }

    // ══════════════════════════════════════════════════
    // UPDATE PATIENT — POST /patients/{id}
    // (HTML forms don't support PUT, so we use POST with the ID in path)
    // ══════════════════════════════════════════════════

    @PostMapping("/{id}")
    public String updatePatient(@PathVariable Long id,
                                 @Valid @ModelAttribute("patient") Patient patient,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        log.info("Updating patient with ID: {}", id);

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Patient");
            return "patient/form";
        }

        try {
            patientService.updatePatient(id, patient);
            redirectAttributes.addFlashAttribute("successMessage", "Patient updated successfully!");
            return "redirect:/patients";
        } catch (Exception e) {
            log.error("Error updating patient: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "patient/form";
        }
    }

    // ══════════════════════════════════════════════════
    // DELETE PATIENT — POST /patients/{id}/delete
    // ══════════════════════════════════════════════════

    @PostMapping("/{id}/delete")
    public String deletePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Deleting patient with ID: {}", id);
        try {
            patientService.deletePatient(id);
            redirectAttributes.addFlashAttribute("successMessage", "Patient deleted successfully.");
        } catch (Exception e) {
            log.error("Error deleting patient {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting patient: " + e.getMessage());
        }
        return "redirect:/patients";
    }
}
