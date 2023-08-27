package com.datacollectorservice.service;

import com.datacollectorservice.exception.CustomException;
import com.datacollectorservice.model.Student;
import com.datacollectorservice.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
            BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    Student student = new Student();

                    student.setStudentName(data[0]);

                    studentRepository.save(student);
                } else throw new CustomException("Format miss match");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
        logger.info("Marks got saved");
    }
}