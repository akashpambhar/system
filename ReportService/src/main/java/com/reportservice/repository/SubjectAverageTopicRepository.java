package com.reportservice.repository;

import com.reportservice.model.SubjectAverageTopic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubjectAverageTopicRepository extends MongoRepository<SubjectAverageTopic, String> {

    List<SubjectAverageTopic> findByClassName(String className);
}
