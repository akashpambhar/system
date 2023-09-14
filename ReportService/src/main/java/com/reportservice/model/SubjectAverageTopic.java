package com.reportservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class SubjectAverageTopic {

    @Id
    private String id;

    private String schoolName;

    private Map<String, Double> subjectAverage;

    private String className;

    private String session;

    @CreatedDate
    private LocalDateTime creationDate;
}
