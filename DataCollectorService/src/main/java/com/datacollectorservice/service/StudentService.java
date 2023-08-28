package com.datacollectorservice.service;

import com.datacollectorservice.exception.CustomException;
import com.datacollectorservice.model.Marks;
import com.datacollectorservice.model.Student;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {

    Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private KafkaTemplate<String, List<Student>> kafkaTemplate;

    public void processJsonMarks(List<Student> students) {
        studentRepository.saveAll(students);
        logger.info("Marks got saved");
        logger.info("Sending message to Kafka topic");
        kafkaTemplate.send("student_marks", students);
    }

    public void processCsvMarks(MultipartFile csvFile) {
        try {
            String content = new String(csvFile.getBytes(), StandardCharsets.UTF_8);
            List<Map<String, Object>> studentMaps = convertCsvToListOfMaps(content);

            List<Student> students = new ArrayList<>();

            for (Map<String, Object> studentMap : studentMaps) {
                String studentName = (String) studentMap.get("studentName");
                List<Map<String, Object>> marks = (List<Map<String, Object>>) studentMap.get("marks");

                Student student = new Student();
                student.setStudentName(studentName);

                List<Marks> marksList = new ArrayList<>();
                for (Map<String, Object> markMap : marks) {
                    String subjectName = (String) markMap.get("subjectName");
                    Double mark = (Double) markMap.get("marks");

                    Marks marksObj = new Marks();
                    marksObj.setSubjectName(subjectName);
                    marksObj.setMarks(mark);
                    marksList.add(marksObj);
                }

                student.setMarks(marksList);
                students.add(student);
            }

            studentRepository.saveAll(students);
            kafkaTemplate.send("student_marks", students);
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

    private List<Map<String, Object>> convertCsvToListOfMaps(String csvContent) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<>() {
            };
            return objectMapper.readValue(csvContent, typeReference);
        } catch (IOException e) {
            logger.error("Error converting CSV content to list of maps:", e);
            throw new RuntimeException("Error converting CSV content", e);
        }
    }
}