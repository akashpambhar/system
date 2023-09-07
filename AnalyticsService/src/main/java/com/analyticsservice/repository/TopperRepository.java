package com.analyticsservice.repository;

import com.analyticsservice.model.Topper;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TopperRepository extends MongoRepository<Topper, String> {
    public Optional<Topper> findFirstTopperByOrderByStudentAverageMarksDesc();
}
