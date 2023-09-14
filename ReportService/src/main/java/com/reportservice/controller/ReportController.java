package com.reportservice.controller;

import com.reportservice.exception.CustomException;
import com.reportservice.model.Student;
import com.reportservice.model.SubjectAverageTopic;
import com.reportservice.repository.SchoolRepository;
import com.reportservice.repository.StudentRepository;
import com.reportservice.repository.SubjectAverageTopicRepository;
import com.reportservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportController {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private SubjectAverageTopicRepository subjectAverageTopicRepository;

    @GetMapping("/student-performance/{className}")
    public List<Student> getStudentPerformance(
            @PathVariable String className) throws CustomException {
        return reportService.getStudentPerformanceWithRespectToClass(className);
    }

    @GetMapping("/students")
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/students/{studentName}")
    public List<Student> getStudentWithName(@PathVariable String studentName) throws CustomException {
        return reportService.getStudentDetailsWithName(studentName);
    }

    @GetMapping("/schoolsTopicAverage/{className}/{subjectName}")
    public List<SubjectAverageTopic> getSchoolsTopicAverage(@PathVariable String className,
                                                            @PathVariable String subjectName)
            throws CustomException {
        return reportService.getSubjectAverageTopic(className, subjectName);
    }

    @GetMapping("/schoolsTopicAverage/{className}")
    public List<SubjectAverageTopic> getSchoolsAllSubjectAverage(@PathVariable String className)
            throws CustomException {
        return reportService.getSchoolsAllSubjectAverage(className);
    }
}
