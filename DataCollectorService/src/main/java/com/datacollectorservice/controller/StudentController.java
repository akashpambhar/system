package com.datacollectorservice.controller;

import com.datacollectorservice.model.Student;
import com.datacollectorservice.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/marks")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/json")
    public String receiveJsonMarks(@RequestBody Student student) {
        studentService.processJsonMarks(student);
        return "JSON data received successfully";
    }

    @PostMapping("/csv")
    public String receiveCsvMarks(@RequestParam("file") MultipartFile csvFile) {
        studentService.processCsvMarks(csvFile);
        return "CSV file received successfully";
    }
}