package com.analyticsservice.repository;

import com.analyticsservice.model.SchoolAverage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolAverageRepository extends MongoRepository<SchoolAverage, String> {

    List<SchoolAverage> findSchoolAveragesByOrderByCreationDateDescSchoolNameAscSchoolAverageDesc();
}
