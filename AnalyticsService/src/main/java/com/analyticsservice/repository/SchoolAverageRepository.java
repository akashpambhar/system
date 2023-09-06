package com.analyticsservice.repository;

import com.analyticsservice.model.SchoolAverage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolAverageRepository extends MongoRepository<SchoolAverage, String> {
}
