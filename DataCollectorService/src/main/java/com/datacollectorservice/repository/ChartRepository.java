package com.datacollectorservice.repository;

import com.datacollectorservice.dto.ChartData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChartRepository extends MongoRepository<ChartData, String> {
}
