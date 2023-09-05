package com.analyticsservice.service;

import com.analyticsservice.model.*;
import com.analyticsservice.repository.SchoolAverageRepository;
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
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "school_average_marks",
            groupId = "school_average_marks_consumer",
            properties = {
                    "spring.json.type.mapping:SchoolAverage:com.analyticsservice.model.SchoolAverageTopic"
            }
    )
    public void listenSchoolAverage(SchoolAverageTopic schoolAverageTopic) {
        logger.info(schoolAverageTopic.toString());
    }

    @KafkaListener(
            topics = "topic_average_marks",
            groupId = "topic_average_marks_consumer",
            properties = {
                    "spring.json.type.mapping:SubjectAverageTopic:com.analyticsservice.model.SubjectAverageTopic"
            }
    )
    public void listenTopicAverage(SubjectAverageTopic subjectAverageTopic) {
        logger.info(subjectAverageTopic.toString());
    }

    @KafkaListener(
            topics = "student_marks",
            groupId = "student-marks-consumer",
            properties = {
                    "spring.json.type.mapping:School:com.analyticsservice.model.School"
            }
    )
    public void calculateAverage(School school) {

        List<Student> students = school.getStudents();
        Map<String, Double> subjectWiseAverage = new HashMap<>();
        double totalAverage = 0;

        // loop through data and calculate averages
        for (Student student : students) {
            double studentTotalMarks = 0;
            List<Marks> marks = student.getMarks();
            for (Marks mark : marks) {
                String subjectName = mark.getSubjectName();
                double subjectMark = mark.getMarks();
                studentTotalMarks += subjectMark;

                if (subjectWiseAverage.get(subjectName) != null)
                    subjectWiseAverage.put(subjectName, subjectWiseAverage.get(subjectName) + subjectMark);
                else
                    subjectWiseAverage.put(subjectName, subjectMark);
            }

            totalAverage += (studentTotalMarks / marks.size());
        }

        totalAverage /= students.size();

        // Calculate subject wise average
        for (Map.Entry<String, Double> entry : subjectWiseAverage.entrySet()) {
            String subjectName = entry.getKey();
            Double TotalMarks = entry.getValue();
            subjectWiseAverage.put(subjectName, TotalMarks / students.size());
        }

        // save to database
        saveSchoolAverageToDatabase(school, subjectWiseAverage, totalAverage);

        // send to kafka topic, topic_average_marks
        sendSubjectWiseAverageToKafkaTopic(school.getSchoolName(), subjectWiseAverage);

        // send to kafka topic, school_average_marks
        sendSchoolAverageToKafkaTopic(school.getSchoolName(), totalAverage);
    }

    public void saveSchoolAverageToDatabase(School school, Map<String, Double> subjectWiseAverage, Double totalAverage) {
        SchoolAverage schoolAverage = new SchoolAverage();
        schoolAverage.setSchoolName(school.getSchoolName());
        schoolAverage.setSubjectAverage(subjectWiseAverage);
        schoolAverage.setSchoolAverage(totalAverage);
        schoolAverageRepository.save(schoolAverage);
    }

    public void sendSchoolAverageToKafkaTopic(String schoolName, double totalAverage) {
        try {
            SchoolAverageTopic schoolAverage = new SchoolAverageTopic();
            schoolAverage.setSchoolName(schoolName);
            schoolAverage.setSchoolAverage(totalAverage);
            kafkaTemplate.send("school_average_marks", schoolAverage);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public void sendSubjectWiseAverageToKafkaTopic(String schoolName, Map<String, Double> subjectWiseAverage) {
        SubjectAverageTopic subjectAverageTopic = new SubjectAverageTopic();
        subjectAverageTopic.setSchoolName(schoolName);
        subjectAverageTopic.setSubjectAverage(subjectWiseAverage);
        kafkaTemplate.send("topic_average_marks", subjectAverageTopic);
    }
}
