package com.hospital.controller;

import com.hospital.model.Doctor;
import com.hospital.service.DoctorService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/doctors")
@Slf4j
public class DoctorController {

    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String listDoctors(@RequestParam(required = false) String search, Model model) {
        List<Doctor> doctors;
        if (search != null && !search.trim().isEmpty()) {
            doctors = doctorService.searchDoctors(search.trim());
            model.addAttribute("search", search);
        } else {
            doctors = doctorService.getAllDoctors();
        }
        model.addAttribute("doctors", doctors);
        return "doctor/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("pageTitle", "Add New Doctor");
        return "doctor/form";
    }

    @PostMapping
    public String saveDoctor(@Valid @ModelAttribute("doctor") Doctor doctor,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Add New Doctor");
            return "doctor/form";
        }
        try {
            doctorService.saveDoctor(doctor);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Dr. " + doctor.getFirstName() + " " + doctor.getLastName() + " added successfully!");
            return "redirect:/doctors";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Add New Doctor");
            return "doctor/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        model.addAttribute("pageTitle", "Edit Doctor");
        return "doctor/form";
    }

    @PostMapping("/{id}")
    public String updateDoctor(@PathVariable Long id,
                                @Valid @ModelAttribute("doctor") Doctor doctor,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Doctor");
            return "doctor/form";
        }
        doctorService.updateDoctor(id, doctor);
        redirectAttributes.addFlashAttribute("successMessage", "Doctor updated successfully!");
        return "redirect:/doctors";
    }

    @PostMapping("/{id}/delete")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            doctorService.deleteDoctor(id);
            redirectAttributes.addFlashAttribute("successMessage", "Doctor removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete doctor: " + e.getMessage());
        }
        return "redirect:/doctors";
    }
}
