package com.nathan.secure_notes.controller;

import com.nathan.secure_notes.dto.requests.LoginRequest;
import com.nathan.secure_notes.dto.requests.SignupRequest;
import com.nathan.secure_notes.dto.responses.LoginResponse;
import com.nathan.secure_notes.dto.responses.MessageResponse;
import com.nathan.secure_notes.dto.responses.UserInfoResponse;
import com.nathan.secure_notes.model.AppRole;
import com.nathan.secure_notes.model.Role;
import com.nathan.secure_notes.model.User;
import com.nathan.secure_notes.repository.RoleRepository;
import com.nathan.secure_notes.repository.UserRepository;
import com.nathan.secure_notes.security.JwtUtils;
import com.nathan.secure_notes.service.UserService;
import com.nathan.secure_notes.service.impl.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserService userService;

    /**
     * Authenticates a user and generates a JWT token
     *
     * This method handles user authentication using the provided login credentials.
     * If the authentication is successful, it generates a JWT token for the user.
     *
     * @param loginRequest The LoginRequest object containing user credentials
     * @return A ResponseEntity containing the LoginResponse with JWT token and user details
     */
    /**
     * Authenticates a user and generates a JWT token
     *
     * This method handles user authentication using the provided login credentials.
     * If the authentication is successful, it generates a JWT token for the user.
     *
     * @param loginRequest The LoginRequest object containing user credentials
     * @return A ResponseEntity containing the LoginResponse with JWT token and user details
     */
    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(userDetails.getId(), userDetails.getUsername(), roles, jwtToken);

        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new user in the application
     *
     * This method handles user registration using the provided signup request.
     * It validates the username, email, and password, and checks if they already exist in the system.
     * If the validation passes, it creates a new user account with the provided information.
     *
     * @param signupRequest The SignupRequest object containing user registration details
     * @return A ResponseEntity containing a MessageResponse indicating the success or failure of the registration
     */
    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if(userRepository.existsByUserName(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: username is already taken"));
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken"));
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword())
        );

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);
                    }
                    case "user" -> {
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                    }
                    default -> {
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                    }
                }
            });
            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);
            user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            user.setAccountExpiryDate(LocalDate.now().plusYears(1));
            user.setTwoFactorEnabled(false);
            user.setSignUpMethod("email");
        }
        user.setRole((Role) roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                roles
        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/username")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "";
    }

}
