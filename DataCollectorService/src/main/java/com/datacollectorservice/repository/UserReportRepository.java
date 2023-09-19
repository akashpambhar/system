package com.datacollectorservice.repository;

import com.datacollectorservice.model.UserReport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserReportRepository extends MongoRepository<UserReport, String> {
    List<UserReport> findAllByUsernameIs(String username);
}
