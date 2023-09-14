package com.analyticsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class SubjectAverageTopic {

    String schoolName;

    Map<String, Double> subjectAverage;

    String className;

    String session;

    @CreatedDate
    private LocalDateTime creationDate;
}
