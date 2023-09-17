package com.datacollectorservice.controller;

import com.datacollectorservice.dto.LoginRequest;
import com.datacollectorservice.dto.SignupRequest;
import com.datacollectorservice.model.security.ERole;
import com.datacollectorservice.model.security.Role;
import com.datacollectorservice.model.security.User;
import com.datacollectorservice.repository.RoleRepository;
import com.datacollectorservice.repository.UserRepository;
import com.datacollectorservice.security.jwt.JwtUtils;
import com.datacollectorservice.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok("{\"token\":\"" + jwtCookie + "\"}");
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setAssignedSchool(signUpRequest.getAssignedSchool());

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_TEACHER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equalsIgnoreCase("superadmin")) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_SUPERUSER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                }
                else if (role.equalsIgnoreCase("admin")) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else if (role.equalsIgnoreCase("teacher")) {
                    Role teacherRole = roleRepository.findByName(ERole.ROLE_TEACHER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(teacherRole);
                } else {
                    throw new RuntimeException("Error: Unknown Role.");
                }

            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>("{\"message\":\"User registered successfully!\"}", HttpStatus.OK);
    }
}