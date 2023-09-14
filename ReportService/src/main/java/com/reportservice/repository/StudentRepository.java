package com.reportservice.repository;

import com.reportservice.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    List<Student> findByStudentName(String studentName);

    List<Student> findByClassNameIgnoreCase(String className);
}
