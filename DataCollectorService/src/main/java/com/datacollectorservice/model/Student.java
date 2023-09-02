package com.datacollectorservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class Student {

    @Id
    private String id;

    @NotBlank(message = "Student name cannot be empty")
    private String studentName;

    @Valid
    private List<Marks> marks;
}