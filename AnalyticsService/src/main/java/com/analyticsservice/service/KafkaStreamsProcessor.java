package com.analyticsservice.service;

import com.analyticsservice.model.*;
import com.analyticsservice.repository.SchoolAverageRepository;
import com.analyticsservice.repository.SubjectAverageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KafkaStreamsProcessor {

    Logger logger = LoggerFactory.getLogger(KafkaStreamsProcessor.class);

    @Autowired
    private SchoolAverageRepository schoolAverageRepository;

    @Autowired
    private SubjectAverageRepository subjectAverageRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "student_marks",
            groupId = "student-marks-consumer",
            properties = {
                    "spring.json.type.mapping:School:com.analyticsservice.model.School"
            }
    )
    public void processStudentMarks(School school) {
        List<Student> students = school.getStudents();

        // Calculate subject-wise averages
        Map<String, Double> subjectMarks = new HashMap<>();

        double totalAverage = 0;

        for (Student student : students) {
            double studentTotalMarks = 0;
            List<Marks> marks = student.getMarks();
            for (Marks mark : marks) {
                String subjectName = mark.getSubjectName();
                double subjectMark = mark.getMarks();
                studentTotalMarks += subjectMark;

                if (subjectMarks.get(subjectName) != null) {
                    subjectMarks.put(subjectName, subjectMarks.get(subjectName) + subjectMark);
                } else
                    subjectMarks.put(subjectName, subjectMark);
            }

            totalAverage += (studentTotalMarks / marks.size());
        }

        // Send subject-wise averages to "subject_average" Kafka topic
        for (Map.Entry<String, Double> entry : subjectMarks.entrySet()) {
            String subjectName = entry.getKey();
            Double subjectAverage = entry.getValue();

            subjectMarks.put(subjectName, subjectAverage / students.size());
            logger.info(subjectMarks.get(subjectName).toString());
        }

        try {
            SubjectAverage subjectAverage = new SubjectAverage();
            subjectAverage.setSchoolName(school.getSchoolName());
            subjectAverage.setSubjectAverage(subjectMarks);
            subjectAverageRepository.save(subjectAverage);
            kafkaTemplate.send("topic_average_marks", subjectAverage);

            SchoolAverage schoolAverage = new SchoolAverage();
            schoolAverage.setSchoolName(school.getSchoolName());
            schoolAverage.setAverage(totalAverage / students.size());
            schoolAverageRepository.save(schoolAverage);
            kafkaTemplate.send("school_average_marks", schoolAverage);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @KafkaListener(
            topics = "school_average_marks",
            groupId = "school_average_marks_consumer",
            properties = {
                    "spring.json.type.mapping:SchoolAverage:com.analyticsservice.model.SchoolAverage"
            }
    )
    public void listenSchoolAverage(SchoolAverage schoolAverage) {
        logger.info(schoolAverage.toString());
    }

    @KafkaListener(
            topics = "topic_average_marks",
            groupId = "topic_average_marks_consumer",
            properties = {
                    "spring.json.type.mapping:SubjectAverage:com.analyticsservice.model.SubjectAverage"
            }
    )
    public void listenTopicAverage(SubjectAverage subjectAverage) {
        logger.info(subjectAverage.toString());
    }
}
