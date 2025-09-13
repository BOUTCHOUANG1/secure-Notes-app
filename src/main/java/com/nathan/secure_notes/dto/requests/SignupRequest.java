package com.nathan.secure_notes.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min =  3, max = 50)
    private String username;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @Getter
    @Setter
    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
