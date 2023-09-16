package com.reportservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reportservice.exception.CustomException;
import com.reportservice.model.Student;
import com.reportservice.model.SubjectAverageTopic;
import com.reportservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/student-performance/class/{className}")
    public List<Student> getStudentPerformance(@PathVariable String className) throws CustomException {
        return reportService.getStudentPerformanceWithRespectToClass(className);
    }

    @GetMapping("/student-performance/all")
    public void getStudents() {
        reportService.getAllStudentPerformance();
    }

    @GetMapping("/student-performance/student/{studentName}")
    public List<Student> getStudentWithName(@PathVariable String studentName) throws CustomException {
        return reportService.getStudentDetailsWithName(studentName);
    }

    @GetMapping("/school-performance/{className}/{subjectName}")
    public List<SubjectAverageTopic> getSchoolsTopicAverage(@PathVariable String className, @PathVariable String subjectName) throws CustomException {
        return reportService.getSubjectAverageTopic(className, subjectName);
    }

    @GetMapping("/school-performance/{className}")
    public List<SubjectAverageTopic> getSchoolsAllSubjectAverage(@PathVariable String className) throws CustomException, JsonProcessingException {
        return reportService.getSchoolsAllSubjectAverage(className);
    }
}