package com.analyticsservice.repository;

import com.analyticsservice.model.SubjectAverage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubjectAverageRepository extends MongoRepository<SubjectAverage, String> {
}
