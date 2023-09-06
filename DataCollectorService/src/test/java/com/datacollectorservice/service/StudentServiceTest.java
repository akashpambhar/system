package com.datacollectorservice.service;

import com.datacollectorservice.dto.ChartData;
import com.datacollectorservice.exception.CustomException;
import com.datacollectorservice.model.School;
import com.datacollectorservice.model.Student;
import com.datacollectorservice.repository.ChartRepository;
import com.datacollectorservice.repository.SchoolRepository;
import com.datacollectorservice.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private ChartRepository chartRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessJsonMarks() {
        // Create a School object and mock behavior
        School school = new School();
        List<Student> students = new ArrayList<>();
        school.setStudents(students);

        // Mock repository save methods
        when(studentRepository.saveAll(students)).thenReturn(students);
        when(schoolRepository.save(school)).thenReturn(school);

        // Call the method under test
        ChartData chartData = studentService.processJsonMarks(school);

        // Verify interactions and assertions
        verify(studentRepository, times(1)).saveAll(students);
        verify(schoolRepository, times(1)).save(school);
        verify(kafkaTemplate, times(1)).send("student_marks", school);
    }

    @Test
    public void testProcessCsvMarks() throws IOException, CustomException {
        // Sample CSV data
        String csvData = """
                School Name,Class,Student Name,Subject Name,Marks,Subject Name,Marks,Subject Name,Marks,,,,
                School 1,A,Akash,Maths,95,Physics,59,Chemistry,21,English,21,Hindi,21
                School 1,A,Savan,Maths,100,Physics,50,Chemistry,20,English,19,Hindi,18
                School 1,B,Dharmik,Maths,95,Physics,59,Chemistry,21,English,21,Hindi,21
                School 1,B,Dharmik,Maths,70,Physics,60,Chemistry,50,English,40,Hindi,30""";

        // Create a mock MultipartFile
        MultipartFile file = new MockMultipartFile("sample.csv", csvData.getBytes());

        // Mock repository save methods
        when(studentRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        when(schoolRepository.save(any(School.class))).thenReturn(new School());

        // Call the method under test
        ChartData chartData = studentService.processCsvMarks(file);

        // Verify interactions and assertions
        verify(studentRepository, times(1)).saveAll(anyList());
        verify(schoolRepository, times(1)).save(any(School.class));
        verify(kafkaTemplate, times(1)).send(eq("student_marks"), any(School.class));
    }

    @Test
    public void testGetStudentById() {
        // Create a sample student ID
        String studentId = "sampleId";

        // Create a sample student object
        Student sampleStudent = new Student();
        sampleStudent.setId(studentId);

        // Mock behavior of the repository
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(sampleStudent));

        // Call the method under test
        Optional<Student> student = studentService.getStudentById(studentId);

        // Assertions
        assertTrue(student.isPresent()); // Ensure that the Optional contains a student
        assertEquals(studentId, student.get().getId()); // Ensure that the returned student has the expected ID

        // Verify interactions
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    public void testSaveToChartData() {
        // Create a sample School object with students
        School school = new School();
        List<Student> students = new ArrayList<>();
        students.add(new Student());
        students.add(new Student());
        school.setStudents(students);

        // Create a ChartData object
        ChartData chartData = new ChartData();
        chartData.setStudentCount(students.size());

        // Mock the behavior of chartRepository.save method
        when(chartRepository.save(any(ChartData.class))).thenReturn(chartData);

        // Call the method under test
        ChartData savedChartData = studentService.saveToChartData(school);

        // Assertions
        assertEquals(chartData, savedChartData);

        // Verify interactions
        verify(chartRepository, times(1)).save(any(ChartData.class));
    }
}
