package com.analyticsservice.service;

import com.analyticsservice.model.*;
import com.analyticsservice.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class KafkaStreamsProcessor {

    Logger logger = LoggerFactory.getLogger(KafkaStreamsProcessor.class);

    @Autowired
    private SchoolAverageRepository schoolAverageRepository;

    @Autowired
    private SchoolReportRepository schoolReportRepository;

    @Autowired
    private StudentReportRepository studentReportRepository;

    @Autowired
    private TopperRepository topperRepository;

    @Autowired
    private SubjectAverageTopicRepository subjectAverageTopicRepository;

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
            topics = "topic_best_improvement_school",
            groupId = "topic_best_improvement_school_consumer",
            properties = {
                    "spring.json.type.mapping:SchoolAverage:com.analyticsservice.model.SchoolAverage"
            }
    )
    public void listenBestImprovementSchool(SchoolAverage schoolAverage) {
        logger.info("School with best improvement : ");
        logger.info(schoolAverage.toString());
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
            topics = "topic_class_wise_topper",
            groupId = "topic_class_wise_topper_consumer",
            properties = {
                    "spring.json.type.mapping:ClassWiseTopperTopic:com.analyticsservice.model.ClassWiseTopperTopic"
            }
    )
    public void listenClassWiseTopperTopic(ClassWiseTopperTopic classWiseTopperTopic) {
        logger.info(classWiseTopperTopic.toString());
    }

    @KafkaListener(
            topics = "topic_school_topper",
            groupId = "topic_school_topper_consumer",
            properties = {
                    "spring.json.type.mapping:TopperOfSchoolTopic:com.analyticsservice.model.TopperOfSchoolTopic"
            }
    )
    public void listenSchoolTopperTopic(TopperOfSchoolTopic topperOfSchoolTopic) {
        logger.info(topperOfSchoolTopic.toString());
    }

    @KafkaListener(
            topics = "topic_topper_among_school",
            groupId = "topic_topper_among_school_consumer",
            properties = {
                    "spring.json.type.mapping:Topper:com.analyticsservice.model.Topper"
            }
    )
    public void listenTopperAmongSchool(Topper topper) {
        logger.info(topper.toString());
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
        logger.info("Here :: ");
        logger.info(school.toString());
        Map<String, Double> subjectWiseAverage = new HashMap<>();
        double totalAverage = 0;

        List<StudentReport> studentReports = new ArrayList<>();
        double studentAverage = 0;

        Map<String, String> classWiseTopper = new HashMap<>();
        Map<String, Double> maxStudentAverageClassWise = new HashMap<>();

        double marksOfTopper = -1;
        String topperOfSchool = "";

        // loop through data and calculate averages
        for (Student student : students) {

            StudentReport studentReport = mapToStudentReport(student);

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

            studentAverage = studentTotalMarks / marks.size();

            if (studentAverage > marksOfTopper) {
                topperOfSchool = student.getStudentName();
                marksOfTopper = studentAverage;
            }

            if (classWiseTopper.get(student.getClassName()) != null) {

                if (maxStudentAverageClassWise.get(student.getClassName()) != null) {
                    double maxStudentAverage = maxStudentAverageClassWise.get(student.getClassName());

                    if (studentAverage > maxStudentAverage) {
                        classWiseTopper.put(student.getClassName(), student.getStudentName());
                        maxStudentAverageClassWise.put(student.getClassName(), studentAverage);
                    }
                }
            } else {
                classWiseTopper.put(student.getClassName(), student.getStudentName());
                maxStudentAverageClassWise.put(student.getClassName(), studentAverage);
            }

            studentReport.setStudentAverage(studentAverage);

            studentReports.add(studentReport);

            totalAverage += studentAverage;
        }

        // send class wise topper to kafka topic
        sendClassWiseTopperToKafka(school.getSchoolName(), classWiseTopper);

        // send topper of the school to kafka topic
        sendSchoolTopperToKafka(school.getSchoolName(), topperOfSchool);

        // save and send, topper among the school to database and kafka topic
        saveAndSendTopperAmongSchoolToKafka(school.getSchoolName(), topperOfSchool, marksOfTopper);

        totalAverage /= students.size();

        // Calculate subject wise average
        for (Map.Entry<String, Double> entry : subjectWiseAverage.entrySet()) {
            String subjectName = entry.getKey();
            Double TotalMarks = entry.getValue();
            subjectWiseAverage.put(subjectName, roundOffAverage(TotalMarks / students.size()));
        }

        // save to database
        saveSchoolAverageToDatabase(school, subjectWiseAverage, totalAverage);

        //save to database
        saveStudentReport(school, studentReports);

        // send to kafka topic, topic_average_marks
        sendSubjectWiseAverageToKafkaTopic(school.getSchoolName(), subjectWiseAverage, school);

        // send to kafka topic, school_average_marks
        sendSchoolAverageToKafkaTopic(school.getSchoolName(), totalAverage);

        findSchoolWithBestImprovement(schoolAverageRepository.findSchoolAveragesByOrderByCreationDateDescSchoolNameAscSchoolAverageDesc());
    }

    public void saveSchoolAverageToDatabase(School school, Map<String, Double> subjectWiseAverage, Double totalAverage) {
        SchoolAverage schoolAverage = new SchoolAverage();
        schoolAverage.setSchoolName(school.getSchoolName());
        schoolAverage.setSubjectAverage(subjectWiseAverage);
        schoolAverage.setClassName(school.getStudents().get(1).getClassName());
        schoolAverage.setSchoolAverage(roundOffAverage(totalAverage));
        schoolAverage.setCreationDate(LocalDateTime.now());
        schoolAverageRepository.save(schoolAverage);
    }

    public void sendSchoolAverageToKafkaTopic(String schoolName, double totalAverage) {
        try {
            SchoolAverageTopic schoolAverage = new SchoolAverageTopic();
            schoolAverage.setSchoolName(schoolName);
            schoolAverage.setSchoolAverage(roundOffAverage(totalAverage));
            kafkaTemplate.send("school_average_marks", schoolAverage);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public void sendSubjectWiseAverageToKafkaTopic(String schoolName, Map<String, Double> subjectWiseAverage, School school) {
        SubjectAverageTopic subjectAverageTopic = new SubjectAverageTopic();
        subjectAverageTopic.setSchoolName(schoolName);
        subjectAverageTopic.setClassName(school.getStudents().get(1).getClassName());
        subjectAverageTopic.setSession(school.getStudents().get(1).getSession());
        subjectAverageTopic.setSubjectAverage(subjectWiseAverage);
        LocalDateTime now = LocalDateTime.now();
        subjectAverageTopic.setCreationDate(now);
        subjectAverageTopicRepository.save(subjectAverageTopic);
        kafkaTemplate.send("topic_average_marks", subjectAverageTopic);
    }

    private void saveStudentReport(School school, List<StudentReport> studentReports) {
        studentReportRepository.saveAll(studentReports);
        SchoolReport schoolReport = new SchoolReport();
        schoolReport.setSchoolName(school.getSchoolName());
        schoolReport.setStudentReports(studentReports);
        schoolReportRepository.save(schoolReport);
    }

    private StudentReport mapToStudentReport(Student student) {
        StudentReport studentReport = new StudentReport();
        studentReport.setStudentName(student.getStudentName());
        studentReport.setClassName(student.getClassName());
        return studentReport;
    }

    private void sendClassWiseTopperToKafka(String schoolName, Map<String, String> classWiseTopper) {
        kafkaTemplate.send("topic_class_wise_topper", new ClassWiseTopperTopic(schoolName, classWiseTopper));
    }

    private void sendSchoolTopperToKafka(String schoolName, String studentName) {
        kafkaTemplate.send("topic_school_topper", new TopperOfSchoolTopic(schoolName, studentName));
    }

    private void saveAndSendTopperAmongSchoolToKafka(String schoolName, String studentName, double studentAverage) {
        Topper topper = new Topper();
        topper.setSchoolName(schoolName);
        topper.setStudentName(studentName);
        topper.setStudentAverageMarks(roundOffAverage(studentAverage));
        topperRepository.save(topper);

        Optional<Topper> topperAmongSchool = topperRepository.findFirstTopperByOrderByStudentAverageMarksDesc();
        topperAmongSchool.get().setStudentAverageMarks(roundOffAverage(topperAmongSchool.get().getStudentAverageMarks()));
        kafkaTemplate.send("topic_topper_among_school", topperAmongSchool.get());
    }

    public void findSchoolWithBestImprovement(List<SchoolAverage> schoolAverageList) {
        List<SchoolAverage> schoolAverages = schoolAverageList;
        SchoolAverage answer = new SchoolAverage();
        boolean isDistinct = true;
        double maxGap = -1000;
        boolean isChecked = false;

        for (int i = 1; i < schoolAverages.size(); i++) {
            SchoolAverage previousSchoolAverage = schoolAverages.get(i - 1);
            SchoolAverage currentSchoolAverage = schoolAverages.get(i);

            if (previousSchoolAverage.getSchoolName().equals(currentSchoolAverage.getSchoolName())) {
                isDistinct = false;
                if (isChecked == false) {
                    isChecked = true;
                    double currentGap = previousSchoolAverage.getSchoolAverage() - currentSchoolAverage.getSchoolAverage();

                    if (currentGap > maxGap) {
                        answer = previousSchoolAverage;
                        maxGap = currentGap;
                    }
                }
            } else
                isChecked = false;
        }

        if (answer.getSchoolName() != null && answer.getSchoolName().isEmpty()) {
            if (isDistinct) {
                answer = findSchoolWithMaxSchoolAverage(schoolAverages).get();
            } else {
                answer = schoolAverages.get(0);
            }
        }

        sendBestSchoolByImprovement(answer);
    }

    private Optional<SchoolAverage> findSchoolWithMaxSchoolAverage(List<SchoolAverage> schoolAverages) {
        return schoolAverages.stream().max(Comparator.comparingDouble(SchoolAverage::getSchoolAverage));
    }

    private void sendBestSchoolByImprovement(SchoolAverage schoolAverage) {
        schoolAverage.setSchoolAverage(roundOffAverage(schoolAverage.getSchoolAverage()));
        kafkaTemplate.send("topic_best_improvement_school", schoolAverage);
    }

    private Double roundOffAverage(Double average){
        if(average != null)
            return Double.valueOf(Math.round(average.doubleValue()));
        return null;
    }
}