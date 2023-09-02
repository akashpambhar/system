package com.datacollectorservice.controller;

import com.datacollectorservice.dto.ChartData;
import com.datacollectorservice.exception.CustomException;
import com.datacollectorservice.model.School;
import com.datacollectorservice.model.Student;
import com.datacollectorservice.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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
    public ChartData receiveJsonMarks(@Valid @RequestBody School school) {
        ChartData chartdata = studentService.processJsonMarks(school);
        simpMessagingTemplate.convertAndSend("/topic/chart-data", chartdata);
        return chartdata;
    }

    @PostMapping(value = "/marks/csv", consumes = "multipart/form-data")
    public ChartData receiveCsvMarks(@RequestParam("file") MultipartFile csvFile) throws CustomException {
        ChartData chartdata = studentService.processCsvMarks(csvFile);
        simpMessagingTemplate.convertAndSend("/topic/chart-data", chartdata);
        return chartdata;
    }

    @GetMapping(value = "/chart")
    public List<ChartData> chartData() {
        return studentService.getChartData();
    }
}