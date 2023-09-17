package com.datacollectorservice.controller;

import com.datacollectorservice.dto.SignupRequest;
import com.datacollectorservice.model.security.ERole;
import com.datacollectorservice.model.security.Role;
import com.datacollectorservice.model.security.User;
import com.datacollectorservice.repository.RoleRepository;
import com.datacollectorservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping()
    public ResponseEntity<List<User>> getAllAdminUsers() {
        try {
            Role role = new Role("64f311c1fd75b44dda4f4b2a", ERole.ROLE_ADMIN);

            List<User> users = userRepository.findAll();

            log.info(users.toString());

            List<User> adminUser = users.stream().filter(user -> user.getRoles().contains(role)).toList();

            return ResponseEntity.ok(adminUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        try {
            List<Role> roles = roleRepository.findAll();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @Valid @RequestBody SignupRequest signUpRequest) {
        try {
            // Find the user by ID
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();

                existingUser.setUsername(signUpRequest.getUsername());
//                existingUser.setPassword(signUpRequest.getPassword());
                existingUser.setAssignedSchool(signUpRequest.getAssignedSchool());

                Set<String> strRoles = signUpRequest.getRoles();
                Set<Role> roles = new HashSet<>();


                strRoles.forEach(role -> {
                    if (role.equalsIgnoreCase("admin")) {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                    } else if (role.equalsIgnoreCase("teacher")) {
                        Role teacherRole = roleRepository.findByName(ERole.ROLE_TEACHER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(teacherRole);
                    }
                    else if (role.equalsIgnoreCase("student")) {
                        Role teacherRole = roleRepository.findByName(ERole.ROLE_TEACHER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(teacherRole);
                    }
                    else {
                        throw new RuntimeException("Error: Unknown Role.");
                    }

                });

                existingUser.setRoles(roles);

                // Save the updated user
                userRepository.save(existingUser);

                return ResponseEntity.ok("User updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the user.");
        }
    }
}
