package com.analyticsservice.repository;

import com.analyticsservice.model.SchoolReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolReportRepository extends MongoRepository<SchoolReport, String> {
}