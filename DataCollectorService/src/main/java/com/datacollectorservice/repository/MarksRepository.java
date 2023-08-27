package com.datacollectorservice.repository;

import com.datacollectorservice.model.Marks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarksRepository extends MongoRepository<Marks, String> {
}
