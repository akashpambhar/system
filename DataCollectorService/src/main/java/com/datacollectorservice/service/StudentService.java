package com.datacollectorservice.service;

import com.datacollectorservice.dto.ChartData;
import com.datacollectorservice.exception.CustomException;
import com.datacollectorservice.model.School;
import com.datacollectorservice.model.Student;
import com.datacollectorservice.repository.SchoolRepository;
import com.datacollectorservice.repository.StudentRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private KafkaTemplate<String, School> kafkaTemplate;

    public void processJsonMarks(School school) {
        studentRepository.saveAll(school.getStudents());
        schoolRepository.save(school);
        logger.info("Marks got saved");
        logger.info("Sending message to Kafka topic");
        kafkaTemplate.send("student_marks", school);
    }

    public void processCsvMarks(MultipartFile csvFile) {
        try {
            String content = new String(csvFile.getBytes(), StandardCharsets.UTF_8);
            School school = convertCsv(content);

            studentRepository.saveAll(school.getStudents());

            schoolRepository.save(school);
            kafkaTemplate.send("student_marks", school);
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

    public ResponseEntity<List<Student>> getStudentByName(String name) throws CustomException {
        List<Student> students = studentRepository.findByStudentNameIgnoreCase(name);
        if (students.isEmpty()) {
            throw new CustomException("Name does not exist");
        }

        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    private School convertCsv(String csvContent) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(csvContent, new TypeReference<>() {
            });
        } catch (IOException e) {
            logger.error("Error converting CSV content to list of maps:", e);
            throw new RuntimeException("Error converting CSV content", e);
        }
    }

    public List<ChartData> getChartData() {
        return schoolRepository.groupByCreationDate();
    }
}