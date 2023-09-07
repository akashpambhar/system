package com.analyticsservice.service;

import com.analyticsservice.model.*;
import com.analyticsservice.repository.SchoolAverageRepository;
import com.analyticsservice.repository.SchoolReportRepository;
import com.analyticsservice.repository.StudentReportRepository;
import com.analyticsservice.repository.TopperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class KafkaStreamsProcessorTest {

    @InjectMocks
    private KafkaStreamsProcessor kafkaStreamsProcessor;

    @Mock
    private SchoolAverageRepository schoolAverageRepository;

    @Mock
    private SchoolReportRepository schoolReportRepository;

    @Mock
    private StudentReportRepository studentReportRepository;

    @Mock
    private TopperRepository topperRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculateAverage() {
        // Create a mock School object with the same structure as the provided object
        School school = new School();
        school.setId("64f9578cd00af25f596b73d6");
        school.setSchoolName("School 1");

        // Create mock students with mock marks
        List<Student> students = new ArrayList<>();

        Student student1 = new Student();
        student1.setStudentName("Dharmik");
        student1.setClassName("A");
        List<Marks> marks1 = Arrays.asList(
                new Marks("Maths", 95.0),
                new Marks("Physics", 59.0),
                new Marks("Chemistry", 81.0),
                new Marks("English", 71.0),
                new Marks("Hindi", 81.0)
        );
        student1.setMarks(marks1);
        students.add(student1);

        Student student2 = new Student();
        student2.setStudentName("Akash");
        student2.setClassName("A");
        List<Marks> marks2 = Arrays.asList(
                new Marks("Maths", 100.0),
                new Marks("Physics", 50.0),
                new Marks("Chemistry", 20.0),
                new Marks("English", 19.0),
                new Marks("Hindi", 91.0)
        );
        student2.setMarks(marks2);
        students.add(student2);

        school.setStudents(students);

        // Mock the behavior of your repository or service methods as needed
        when(schoolAverageRepository.save(any())).thenReturn(new SchoolAverage());
        when(schoolReportRepository.save(any())).thenReturn(new SchoolReport());
        when(studentReportRepository.saveAll(any())).thenReturn(new ArrayList<>());
        when(topperRepository.findFirstTopperByOrderByStudentAverageMarksDesc()).thenReturn(Optional.of(new Topper()));
        when(schoolAverageRepository.findSchoolAveragesByOrderByCreationDateDescSchoolNameAscSchoolAverageDesc()).thenReturn(new ArrayList<>());

        // Call the method to be tested
        kafkaStreamsProcessor.calculateAverage(school);

        // Verify that the expected repository or service methods were called
        verify(schoolAverageRepository, times(1)).save(any());
        verify(schoolReportRepository, times(1)).save(any());
        verify(studentReportRepository, times(1)).saveAll(any());
        verify(topperRepository, times(1)).findFirstTopperByOrderByStudentAverageMarksDesc();
        verify(schoolAverageRepository, times(1)).findSchoolAveragesByOrderByCreationDateDescSchoolNameAscSchoolAverageDesc();

        // Verify that the expected KafkaProducer methods were called
        verify(kafkaTemplate, times(1)).send(eq("topic_class_wise_topper"), any(ClassWiseTopperTopic.class));
        verify(kafkaTemplate, times(1)).send(eq("topic_school_topper"), any(TopperOfSchoolTopic.class));
        verify(kafkaTemplate, times(1)).send(eq("topic_topper_among_school"), any(Topper.class));
        verify(kafkaTemplate, times(1)).send(eq("school_average_marks"), any(SchoolAverageTopic.class));
        verify(kafkaTemplate, times(1)).send(eq("topic_average_marks"), any(SubjectAverageTopic.class));
        verify(kafkaTemplate, times(1)).send(eq("topic_best_improvement_school"), any(SchoolAverage.class));
    }

}
