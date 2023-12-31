package com.datacollectorservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class SchoolAverage {

    @Id
    String id;

    String schoolName;

    Double schoolAverage;

    Map<String, Double> subjectAverage;
}
