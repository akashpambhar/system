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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
