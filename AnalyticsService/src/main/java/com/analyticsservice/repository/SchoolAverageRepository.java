package com.analyticsservice.repository;

import com.analyticsservice.model.SchoolAverage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SchoolAverageRepository extends MongoRepository<SchoolAverage, String> {
}
