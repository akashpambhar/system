package com.datacollectorservice.repository;

import com.datacollectorservice.dto.ChartData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartRepository extends MongoRepository<ChartData, String> {
}
