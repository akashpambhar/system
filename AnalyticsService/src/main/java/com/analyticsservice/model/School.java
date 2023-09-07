package com.analyticsservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class School {

    @Id
    private String id;

    @NotBlank(message = "School name cannot be empty")
    private String schoolName;

    @DBRef
    @Valid
    private List<Student> students;

    @CreatedDate
    private LocalDateTime creationDate;
}
