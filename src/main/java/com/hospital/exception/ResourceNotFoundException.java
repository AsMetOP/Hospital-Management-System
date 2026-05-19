package com.hospital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CUSTOM EXCEPTION CLASS
 *
 * WHY A CUSTOM EXCEPTION?
 * When a patient/doctor with a given ID doesn't exist, we want to:
 *   1. Return HTTP 404 (Not Found) — not 500 (Internal Server Error)
 *   2. Show a meaningful message to the user
 *
 * @ResponseStatus(HttpStatus.NOT_FOUND):
 *   When Spring catches this exception, it automatically sends HTTP 404.
 *   Without this, Spring would send HTTP 500 for any unhandled exception.
 *
 * extends RuntimeException:
 *   RuntimeException = unchecked exception → you don't have to declare
 *   "throws ResourceNotFoundException" on every method. Cleaner code.
 *
 * JD REQUIREMENT: "Fix issues/defects in any part of an application"
 * (proper exception handling is part of writing defect-free code)
 *
 * INTERVIEW TIP: "I used custom exceptions to decouple error handling from
 * business logic. The @ResponseStatus annotation maps domain errors to
 * appropriate HTTP status codes following REST best practices."
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        // Calls RuntimeException(message) constructor
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    // Getters (useful for logging and error responses)
    public String getResourceName() { return resourceName; }
    public String getFieldName()    { return fieldName; }
    public Object getFieldValue()   { return fieldValue; }
}
