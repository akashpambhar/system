package com.datacollectorservice.controller;

import com.datacollectorservice.dto.ChartData;
import com.datacollectorservice.exception.CustomException;
import com.datacollectorservice.model.School;
import com.datacollectorservice.model.SchoolAverage;
import com.datacollectorservice.model.Student;
import com.datacollectorservice.model.UserReport;
import com.datacollectorservice.repository.UserReportRepository;
import com.datacollectorservice.service.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "swaggerauth")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/student")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/student/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Student> getStudentById(@PathVariable String id) {
        return studentService.getStudentById(id);
    }

    @GetMapping("/student/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Student>> getStudentByName(@PathVariable String name) throws CustomException {
        return studentService.getStudentByName(name);
    }

    @PostMapping("/marks/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ChartData receiveJsonMarks(@Valid @RequestBody School school) {
        ChartData chartdata = studentService.processJsonMarks(school);
        simpMessagingTemplate.convertAndSend("/topic/chart-data", chartdata);
        return chartdata;
    }

    @PostMapping(value = "/marks/csv", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ChartData receiveCsvMarks(@RequestParam("file") MultipartFile csvFile) throws CustomException {
        ChartData chartdata = studentService.processCsvMarks(csvFile);
        simpMessagingTemplate.convertAndSend("/topic/chart-data", chartdata);
        return chartdata;
    }

    @GetMapping(value = "/chart")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ChartData> chartData() {
        return studentService.getChartData();
    }

    @GetMapping("/average")
    @PreAuthorize("hasRole('TEACHER')")
    public List<SchoolAverage> getSchoolAverages() {
        return studentService.getSchoolAverages();
    }

    @GetMapping("/school/upload-data")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserReport> getUploadInformation(){
        List<UserReport> userReports = studentService.getAllUserReportByUsername();
        simpMessagingTemplate.convertAndSend("/topic/upload-data", userReports);
        return userReports;
    }

    @GetMapping("/school")
    public Set<String> getSchoolList(){
        return studentService.getSchools();
    }

    @GetMapping("/school/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public List<String> getSchoolListOfLoggedInUser(){
        return studentService.getSchoolOfLoggedInUser();
    }

    @GetMapping("/student/school/{schoolName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public List<Student> getAllStudentsFromSchool(@PathVariable String schoolName) {
        return studentService.getAllStudentsFromSchool(schoolName);
    }

    @GetMapping("/student/dashboard/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public List<Student> getStudentsForStudentDashboard(@PathVariable String studentId){
        return studentService.getStudentsForStudentDashboard(studentId);
    }

    @GetMapping("/student/toppers/{schoolName}")
    @PreAuthorize("hasRole('TEACHER')")
    public Map<String, Student> getClassWiseToppersFromSchool(@PathVariable String schoolName){
        return studentService.getClassWiseToppersFromSchool(schoolName);
    }
}