package com.datacollectorservice.service;

import com.datacollectorservice.dto.ChartData;
import com.datacollectorservice.exception.CustomException;
import com.datacollectorservice.model.Marks;
import com.datacollectorservice.model.School;
import com.datacollectorservice.model.SchoolAverage;
import com.datacollectorservice.model.Student;
import com.datacollectorservice.repository.ChartRepository;
import com.datacollectorservice.repository.SchoolAverageRepository;
import com.datacollectorservice.repository.SchoolRepository;
import com.datacollectorservice.repository.StudentRepository;
import com.opencsv.CSVReader;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    private ChartRepository chartRepository;

    @Autowired
    private SchoolAverageRepository schoolAverageRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public ChartData processJsonMarks(School school) {
        studentRepository.saveAll(school.getStudents());
        schoolRepository.save(school);
        ChartData chartdata = saveToChartData(school);
        logger.info("Marks got saved");
        logger.info("Sending message to Kafka topic");
        kafkaTemplate.send("student_marks", school);
        return chartdata;
    }

    public ChartData processCsvMarks(MultipartFile file) throws CustomException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext(); // Read the header row
            String[] line;

            School school = new School();
            List<Student> students = new ArrayList<>();

            while ((line = reader.readNext()) != null) {
                school.setSchoolName(line[0]);
                school.setClassName(line[1]);

                Student student = new Student();
                student.setStudentName(line[2]);

                List<Marks> marksList = new ArrayList<>();
                for (int i = 3; i < line.length - 1; i += 2) {
                    String subjectName = line[i];
                    String marks = line[i + 1];

                    if (subjectName == null || subjectName.isEmpty() || marks == null || marks.isEmpty()) {
                        throw new CustomException("Subject name or marks cannot be empty");
                    }
                    Marks mark = new Marks();
                    mark.setSubjectName(subjectName);
                    mark.setMarks(Double.parseDouble(marks));
                    marksList.add(mark);
                }

                student.setMarks(marksList);
                students.add(student);
            }
            studentRepository.saveAll(students);
            school.setStudents(students);

            schoolRepository.save(school);
            kafkaTemplate.send("student_marks", school);

            ChartData chartData = saveToChartData(school);
            return chartData;
        } catch (IOException e) {
            logger.error("An IO exception occurred while processing the CSV file:", e);
            throw new RuntimeException("Error processing CSV file", e);
        } catch (ConstraintViolationException e) {
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred while processing the CSV content:", e);
            throw new RuntimeException("Error processing CSV content", e);
        }
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

    public List<ChartData> getChartData() {
        return chartRepository.findAll();
    }

    public List<SchoolAverage> getSchoolAverages(){
        return schoolAverageRepository.findAll();
    }

    private ChartData saveToChartData(School school) {
        ChartData chartData = new ChartData();
        chartData.setStudentCount(school.getStudents().size());
        return chartRepository.save(chartData);
    }
}