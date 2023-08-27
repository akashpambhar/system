package com.datacollectorservice.service;

import com.datacollectorservice.exception.CustomException;
import com.datacollectorservice.model.Marks;
import com.datacollectorservice.model.Student;
import com.datacollectorservice.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {

    Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    public void processJsonMarks(Student student) {
        studentRepository.save(student);
        logger.info("Marks got saved");
    }

    public void processCsvMarks(MultipartFile csvFile) {
        try {
            String content = new String(csvFile.getBytes(), StandardCharsets.UTF_8);
            Map<String, Object> jsonMap = convertToJson(content);

            Student student = new Student();
            student.setStudentName(jsonMap.get("studentName").toString());
            student.setMarks((List<Marks>) jsonMap.get("marks"));

            studentRepository.save(student);
        } catch (IOException e) {
            logger.error("An IO exception occurred while processing the CSV file:", e);
            throw new RuntimeException("Error processing CSV file", e);
        } catch (Exception e) {
            logger.error("An error occurred while processing the CSV content:", e);
            throw new RuntimeException("Error processing CSV content", e);
        }

        logger.info("Marks got saved");
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(String Id) {
        return studentRepository.findById(Id);
    }

    public ResponseEntity<Optional<Student>> getStudentByName(String Name) throws CustomException {

        Optional<Student> student = studentRepository.findByStudentNameIgnoreCase(Name);

        if (student.isEmpty()) {
            throw new CustomException("Name does not exist");
        }

        return new ResponseEntity<>(student, HttpStatus.OK);

    }

    public Map<String, Object> convertToJson(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, Map.class);
    }
}