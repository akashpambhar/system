package com.datacollectorservice.repository;

import com.datacollectorservice.model.security.ERole;
import com.datacollectorservice.model.security.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}