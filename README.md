# 🏥 MediCare — Hospital Management System

> A full-stack Hospital Management System built with **Spring MVC**, **Spring Boot**, **MySQL**, **Bootstrap 5**, and **Thymeleaf**. Covers CRUD operations for Patients, Doctors, and Appointments with full unit test coverage using **JUnit 5** and **Mockito**.

---

## 📸 Features

| Module | Features |
|---|---|
| **Dashboard** | Live stats (patients, doctors, appointments), today's appointment table |
| **Patients** | Add, View, Edit, Delete patients · Search by name · Form validation |
| **Doctors** | Add, View, Edit, Delete doctors · Filter by specialization · Availability toggle |
| **Appointments** | Book appointments · Mark complete · Cancel · Doctor double-booking prevention |

---

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 17, Spring Boot 3.2, Spring MVC |
| **Database** | MySQL 8.0, Spring Data JPA, Hibernate |
| **Frontend** | Thymeleaf, HTML5, CSS3, JavaScript, Bootstrap 5 |
| **Testing** | JUnit 5, Mockito, MockMvc, AssertJ |
| **Code Coverage** | Jacoco Maven Plugin |
| **Logging** | SLF4J + Logback (via Lombok `@Slf4j`) |
| **Build** | Maven |

---

## 🏗️ Project Architecture

```
com.hospital
├── HospitalApplication.java       ← Spring Boot entry point
│
├── model/                         ← Entity classes (JPA ↔ MySQL)
│   ├── Patient.java
│   ├── Doctor.java
│   └── Appointment.java
│
├── repository/                    ← Data Access Layer (JpaRepository)
│   ├── PatientRepository.java
│   ├── DoctorRepository.java
│   └── AppointmentRepository.java
│
├── service/                       ← Business Logic (interfaces)
│   ├── PatientService.java
│   ├── DoctorService.java
│   └── AppointmentService.java
│
├── service/impl/                  ← Business Logic (implementations)
│   ├── PatientServiceImpl.java    ← @Service, @Transactional, @Slf4j
│   ├── DoctorServiceImpl.java
│   └── AppointmentServiceImpl.java
│
├── controller/                    ← Spring MVC Controllers
│   ├── DashboardController.java   ← @Controller, @GetMapping
│   ├── PatientController.java     ← Full CRUD, PRG Pattern
│   ├── DoctorController.java
│   └── AppointmentController.java
│
└── exception/
    └── ResourceNotFoundException.java  ← Custom 404 exception
```

### Request Flow (Spring MVC)

```
Browser → DispatcherServlet → @Controller → @Service → @Repository → MySQL
                                                  ↑
                                        Business logic here
```

---

## 🗄️ Database Design

```
┌─────────────┐         ┌─────────────────┐         ┌──────────────┐
│   patients  │         │  appointments   │         │   doctors    │
├─────────────┤         ├─────────────────┤         ├──────────────┤
│ id (PK)     │◄────────│ patient_id (FK) │         │ id (PK)      │
│ first_name  │         │ doctor_id  (FK) │────────►│ first_name   │
│ last_name   │         │ appointment_date│         │ last_name    │
│ email       │         │ status          │         │ email        │
│ phone       │         │ notes           │         │ specialization│
│ blood_group │         │ created_at      │         │ experience   │
│ gender      │         └─────────────────┘         │ available    │
└─────────────┘                                     └──────────────┘

Relationships:
  patients  (1) ──────── (Many) appointments (Many) ──────── (1) doctors
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0

### Step 1 — Create Database
```sql
CREATE DATABASE hospital_db;
```

### Step 2 — Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### Step 3 — Run Application
```bash
git clone https://github.com/YOUR_USERNAME/hospital-management.git
cd hospital-management
mvn spring-boot:run
```

### Step 4 — Open in Browser
```
http://localhost:8080
```

---

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run tests + generate Jacoco coverage report
mvn test jacoco:report

# View coverage report at:
# target/site/jacoco/index.html
```

### Test Coverage Summary

| Class | Tests | Coverage |
|---|---|---|
| PatientServiceImpl | 10 | ~90% |
| DoctorServiceImpl | 5 | ~85% |
| PatientController | 6 | ~80% |

---

## 🔑 Key Design Decisions

| Decision | Reason |
|---|---|
| Interface + Impl pattern for services | Follows Dependency Inversion (SOLID). Controllers depend on abstraction, not implementation. Enables Mockito mocking in tests. |
| Constructor injection over field injection | Explicit dependencies, easier to test, immutable fields |
| Custom ResourceNotFoundException | Clean 404 responses instead of 500 server errors |
| PRG Pattern (Post-Redirect-Get) | Prevents duplicate form submissions on browser refresh |
| @Transactional on service methods | Ensures data integrity; rollback on failures |
| H2 for test scope | Tests run without MySQL dependency; fast and isolated |

---

## 📋 API Endpoints (Spring MVC Routes)

| Method | URL | Description |
|---|---|---|
| GET | `/` | Dashboard |
| GET | `/patients` | List all patients |
| GET | `/patients/new` | Add patient form |
| POST | `/patients` | Save new patient |
| GET | `/patients/{id}` | View patient |
| GET | `/patients/{id}/edit` | Edit patient form |
| POST | `/patients/{id}` | Update patient |
| POST | `/patients/{id}/delete` | Delete patient |
| GET | `/doctors` | List doctors |
| GET | `/appointments` | List appointments |
| GET | `/appointments/new` | Book appointment form |
| POST | `/appointments` | Book appointment |
| POST | `/appointments/{id}/status` | Update status |
| POST | `/appointments/{id}/cancel` | Cancel appointment |

---

## 👨‍💻 Author

**Asmet Ranjan Sahoo**  
Data Engineering Student, KIIT University  
GitHub: [github.com/YOUR_USERNAME](https://github.com/AsMetOP)
