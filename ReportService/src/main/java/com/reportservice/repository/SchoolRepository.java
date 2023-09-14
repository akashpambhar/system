package com.reportservice.repository;

import com.reportservice.model.School;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SchoolRepository extends MongoRepository<School, String> {

    List<School> findBySchoolNameIgnoreCase(String name);
}
