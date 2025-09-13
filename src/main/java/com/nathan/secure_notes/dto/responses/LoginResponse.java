package com.nathan.secure_notes.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LoginResponse {
    private Long id;
    private String jwtToken;

    private String username;
    private List<String> roles;

    public LoginResponse(Long id, String username, List<String> roles, String jwtToken) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
    }

}


