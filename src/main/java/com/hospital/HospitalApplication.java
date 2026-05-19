package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * APPLICATION ENTRY POINT
 *
 * @SpringBootApplication is a meta-annotation that combines:
 *   1. @Configuration        — this class can define Spring beans
 *   2. @EnableAutoConfiguration — Spring Boot configures itself based on classpath
 *                                 (sees MySQL driver → configures DataSource automatically)
 *                                 (sees Thymeleaf → configures template engine automatically)
 *   3. @ComponentScan        — scans this package and all sub-packages for:
 *                                 @Controller, @Service, @Repository, @Component
 *
 * HOW SPRING BOOT STARTS:
 *   1. SpringApplication.run() creates the Spring ApplicationContext (IoC container)
 *   2. IoC container scans for all annotated classes
 *   3. Creates beans, injects dependencies
 *   4. Starts embedded Tomcat on port 8080
 *   5. Registers all @RequestMapping routes
 *   6. App is ready to serve HTTP requests
 *
 * INTERVIEW TIP: "IoC (Inversion of Control) means Spring creates and manages
 * objects for you. You don't call 'new Service()', Spring injects it via DI."
 */
@SpringBootApplication
public class HospitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalApplication.class, args);
        System.out.println("✅ Hospital Management System started at: http://localhost:8080");
    }
}
