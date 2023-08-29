package com.datacollectorservice.controller;

import com.datacollectorservice.dto.ChartData;
import com.datacollectorservice.exception.CustomException;
import com.datacollectorservice.model.School;
import com.datacollectorservice.model.Student;
import com.datacollectorservice.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/student")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/student/id/{id}")
    public Optional<Student> getStudentById(@PathVariable String id) {
        return studentService.getStudentById(id);
    }

    @GetMapping("/student/name/{name}")
    public ResponseEntity<List<Student>> getStudentByName(@PathVariable String name) throws CustomException {
        return studentService.getStudentByName(name);
    }

    @PostMapping("/marks/json")
    public String receiveJsonMarks(@RequestBody School school) {
        studentService.processJsonMarks(school);
        return "JSON data received successfully";
    }

    @PostMapping(value = "/marks/csv", consumes = "multipart/form-data")
    public String receiveCsvMarks(@RequestParam("file") MultipartFile csvFile) {
        studentService.processCsvMarks(csvFile);
        return "CSV file received successfully";
    }

    @GetMapping(value = "/chart")
    public List<ChartData> chartData(){
        return studentService.getChartData();
    }
}