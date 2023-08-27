package com.datacollectorservice.repository;

import com.datacollectorservice.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    public Optional<Student> findByStudentNameIgnoreCase(String name);
}