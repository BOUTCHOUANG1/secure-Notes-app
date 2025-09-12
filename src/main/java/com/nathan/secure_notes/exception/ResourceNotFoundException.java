package com.nathan.secure_notes.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException{
    String ResourceName;
    String fieldName;
    Long fieldId;
    String value;

    public ResourceNotFoundException(String resourceName, String fieldName, String value) {
        super(String.format("%s not found with %s : %s", resourceName, fieldName, value));
        this.ResourceName = resourceName;
        this.fieldName = fieldName;
        this.value = value;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldId) {
        super(String.format("%s not found with %s : %d", resourceName, fieldName, fieldId));
        this.ResourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldId = fieldId;
    }
}
