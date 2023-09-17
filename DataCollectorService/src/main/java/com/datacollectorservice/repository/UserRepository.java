package com.datacollectorservice.repository;

import com.datacollectorservice.model.security.Role;
import com.datacollectorservice.model.security.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    List<User> findAllByRolesIs(Set<Role> roles);
}