package com.analyticsservice.repository;

import com.analyticsservice.model.SubjectAverageTopic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubjectAverageTopicRepository extends MongoRepository<SubjectAverageTopic, String> {
}
