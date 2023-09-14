package com.datacollectorservice.repository;

import com.datacollectorservice.model.UserReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserReportRepository extends MongoRepository<UserReport, String> {
}
