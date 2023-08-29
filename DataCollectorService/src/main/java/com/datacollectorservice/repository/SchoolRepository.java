package com.datacollectorservice.repository;

import com.datacollectorservice.dto.ChartData;
import com.datacollectorservice.model.School;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolRepository extends MongoRepository<School, String> {

    @Aggregation(pipeline = {
            "{$addFields: { studentCount: { $size: '$students' } } }",
            "{$group: {_id: '$creatōionDate', studentCount: { $sum: '$studentCount' }}}",
            "{$sort: { _id: 1 }}"
    })
    public List<ChartData> groupByCreationDate();
}
