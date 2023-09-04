package com.datacollectorservice.repository;

import com.datacollectorservice.model.SchoolAverage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SchoolAverageRepository extends MongoRepository<SchoolAverage, String> {
}
