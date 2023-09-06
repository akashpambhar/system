package com.analyticsservice.repository;

import com.analyticsservice.model.StudentReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentReportRepository extends MongoRepository<StudentReport, String> {
}
