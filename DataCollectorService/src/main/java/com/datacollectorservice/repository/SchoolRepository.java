package com.datacollectorservice.repository;

import com.datacollectorservice.dto.ChartData;
import com.datacollectorservice.model.School;
import com.datacollectorservice.model.Student;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends MongoRepository<School, String> {

    @Aggregation(pipeline = {
            "{$group: {_id: \"$creationDate\", count: { $sum: 1 } }}"
    })
    public List<ChartData> groupByCreationDate();
}
