package com.analyticsservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class StudentReport {

    @Id
    private String id;

    @NotBlank(message = "Student name cannot be empty")
    private String studentName;

    private String className;

    private double studentAverage;
}

