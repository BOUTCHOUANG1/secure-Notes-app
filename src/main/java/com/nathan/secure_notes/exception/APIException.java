package com.nathan.secure_notes.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class APIException extends RuntimeException{
    public static Long serialVersionUID = 1L;

    public APIException(String message) {
        super(message);
    }
}
