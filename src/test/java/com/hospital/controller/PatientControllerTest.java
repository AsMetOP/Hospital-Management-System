package com.hospital.controller;

import com.hospital.model.Patient;
import com.hospital.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * CONTROLLER LAYER TESTS using MockMvc
 *
 * JD REQUIREMENT: "Write unit test cases for a given code using JUnit and Mockito"
 *
 * @WebMvcTest(PatientController.class):
 *   Loads ONLY the web layer (controllers, filters, MockMvc).
 *   Does NOT load the full ApplicationContext — no real database, no services.
 *   Much faster than @SpringBootTest.
 *
 * @MockBean:
 *   Like @Mock (Mockito) but registers the mock in the Spring context.
 *   Required here because PatientController has @Autowired PatientService.
 *   Spring injects this mock into the controller automatically.
 *
 * MockMvc:
 *   Simulates HTTP requests without starting a real server.
 *   Lets you test: HTTP status codes, view names, model attributes, redirects.
 *
 * STATIC IMPORTS EXPLAINED:
 *   get("/patients")                     → builds a GET request
 *   post("/patients")                    → builds a POST request
 *   status().isOk()                      → expects HTTP 200
 *   status().is3xxRedirection()          → expects HTTP 3xx redirect
 *   view().name("patient/list")          → expects this Thymeleaf template
 *   model().attributeExists("patients")  → model has "patients" attribute
 *   redirectedUrl("/patients")           → redirect goes to /patients
 *
 * INTERVIEW TIP: "@WebMvcTest is for testing the web layer in isolation.
 * I use @MockBean to mock the service dependencies so controller tests
 * don't require a database or a full Spring context."
 */
@WebMvcTest(PatientController.class)
@DisplayName("PatientController Unit Tests")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;  // injected automatically by @WebMvcTest

    @MockBean
    private PatientService patientService;  // fake service injected into controller

    private Patient samplePatient;
    private List<Patient> patientList;

    @BeforeEach
    void setUp() {
        samplePatient = Patient.builder()
                .id(1L)
                .firstName("Raj")
                .lastName("Sharma")
                .email("raj@email.com")
                .phone("9876543210")
                .gender("Male")
                .build();

        Patient patient2 = Patient.builder()
                .id(2L)
                .firstName("Priya")
                .lastName("Singh")
                .email("priya@email.com")
                .phone("9876543211")
                .build();

        patientList = Arrays.asList(samplePatient, patient2);
    }

    // ════════════════════════════════════════════════════════
    // GET /patients — List Page
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /patients should return patient list page with 200 OK")
    void listPatients_ShouldReturn200AndPatientListView() throws Exception {
        // GIVEN — service returns our test patients list
        given(patientService.getAllPatients()).willReturn(patientList);

        // WHEN + THEN — send GET request and assert response
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())                            // HTTP 200
                .andExpect(view().name("patient/list"))                // correct template
                .andExpect(model().attributeExists("patients"))        // model has "patients"
                .andExpect(model().attribute("patients", hasSize(2)));  // 2 patients in list
    }

    @Test
    @DisplayName("GET /patients with search param should call searchPatients")
    void listPatients_WithSearchParam_ShouldCallSearchService() throws Exception {
        // GIVEN
        given(patientService.searchPatients("Raj")).willReturn(List.of(samplePatient));

        // WHEN + THEN
        mockMvc.perform(get("/patients").param("search", "Raj"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/list"))
                .andExpect(model().attribute("search", "Raj"))
                .andExpect(model().attribute("patients", hasSize(1)));

        then(patientService).should(times(1)).searchPatients("Raj");
        then(patientService).should(never()).getAllPatients();
    }

    // ════════════════════════════════════════════════════════
    // GET /patients/new — Add Form
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /patients/new should return the add patient form")
    void showAddForm_ShouldReturnFormView() throws Exception {
        mockMvc.perform(get("/patients/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/form"))
                .andExpect(model().attributeExists("patient"));  // empty patient in model
    }

    // ════════════════════════════════════════════════════════
    // POST /patients — Save Patient
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /patients with valid data should redirect to /patients")
    void savePatient_WithValidData_ShouldRedirectToPatientList() throws Exception {
        // GIVEN
        given(patientService.savePatient(any(Patient.class))).willReturn(samplePatient);

        // WHEN + THEN
        // .param() simulates form field submission
        mockMvc.perform(post("/patients")
                        .param("firstName", "Raj")
                        .param("lastName", "Sharma")
                        .param("email", "raj@email.com")
                        .param("phone", "9876543210"))
                .andExpect(status().is3xxRedirection())       // redirect (302)
                .andExpect(redirectedUrl("/patients"));        // to /patients
    }

    @Test
    @DisplayName("POST /patients with invalid data should return form with errors")
    void savePatient_WithInvalidData_ShouldReturnFormWithErrors() throws Exception {
        // WHEN + THEN — submit form with blank required fields
        mockMvc.perform(post("/patients")
                        .param("firstName", "")    // blank — violates @NotBlank
                        .param("lastName", "")     // blank
                        .param("email", "bad-email") // invalid email format
                        .param("phone", "123"))    // too short — violates @Pattern
                .andExpect(status().isOk())             // stays on form (200, not redirect)
                .andExpect(view().name("patient/form")) // same form view returned
                .andExpect(model().hasErrors());        // model contains validation errors
    }

    // ════════════════════════════════════════════════════════
    // GET /patients/{id} — View Patient
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /patients/{id} should return patient view when patient exists")
    void viewPatient_WhenPatientExists_ShouldReturnViewPage() throws Exception {
        given(patientService.getPatientById(1L)).willReturn(samplePatient);

        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/view"))
                .andExpect(model().attribute("patient", samplePatient));
    }

    // ════════════════════════════════════════════════════════
    // POST /patients/{id}/delete — Delete Patient
    // ════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /patients/{id}/delete should delete and redirect")
    void deletePatient_ShouldDeleteAndRedirect() throws Exception {
        // GIVEN — deletePatient is void, so no willReturn needed
        willDoNothing().given(patientService).deletePatient(1L);

        mockMvc.perform(post("/patients/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        then(patientService).should(times(1)).deletePatient(1L);
    }
}
