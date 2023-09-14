package com.reportservice.service;

import com.reportservice.exception.CustomException;
import com.reportservice.model.ReportRequest;
import com.reportservice.model.Student;
import com.reportservice.model.SubjectAverageTopic;
import com.reportservice.repository.SchoolRepository;
import com.reportservice.repository.StudentRepository;
import com.reportservice.repository.SubjectAverageTopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectAverageTopicRepository subjectAverageTopicRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public List<Student> getStudentPerformanceWithRespectToClass(String className) throws CustomException {
        List<Student> students = studentRepository.findByClassNameIgnoreCase(className);
        if (students.isEmpty()) {
            throw new CustomException("class name not found");
        }
        kafkaTemplate.send("report_requests", new ReportRequest(students.getClass().getName(), students));
        return students;
    }

    public List<Student> getStudentDetailsWithName(String studentName) throws CustomException {
        List<Student> students = studentRepository.findByStudentName(studentName);
        if (!students.isEmpty()) {
            kafkaTemplate.send("report_requests", new ReportRequest(students.getClass().getName(), students));
            return students;
        } else throw new CustomException("Student name not found");
    }

    public List<SubjectAverageTopic> getSubjectAverageTopic(String className, String subjectName) throws CustomException {
        List<SubjectAverageTopic> SubjectAverageTopicList =
                subjectAverageTopicRepository.findByClassName(className);

        if (!SubjectAverageTopicList.isEmpty()) {
            List<SubjectAverageTopic> subjectAverageTopicslist1 = new ArrayList<>();
            for (int i = 0; i < SubjectAverageTopicList.size(); i++) {
                SubjectAverageTopic subjectAverageTopic = new SubjectAverageTopic();
                Map<String, Double> filteredMap = SubjectAverageTopicList.get(i).getSubjectAverage().entrySet()
                        .stream().filter(entry -> entry.getKey().equalsIgnoreCase(subjectName)).
                        collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                subjectAverageTopic.setSubjectAverage(filteredMap);
                subjectAverageTopic.setSession(SubjectAverageTopicList.get(i).getSession());
                subjectAverageTopic.setClassName(SubjectAverageTopicList.get(i).getClassName());
                subjectAverageTopic.setSchoolName(SubjectAverageTopicList.get(i).getSchoolName());
                subjectAverageTopic.setCreationDate(SubjectAverageTopicList.get(i).getCreationDate());
                subjectAverageTopicslist1.add(subjectAverageTopic);
            }
            kafkaTemplate.send("report_requests", new ReportRequest(subjectAverageTopicslist1.getClass().getName(), subjectAverageTopicslist1));
            return subjectAverageTopicslist1;
        } else throw new CustomException("Class name not found");

    }

    public List<SubjectAverageTopic> getSchoolsAllSubjectAverage(String className) throws CustomException {
        List<SubjectAverageTopic> SubjectAverageTopicList =
                subjectAverageTopicRepository.findByClassName(className);
        if (!SubjectAverageTopicList.isEmpty()) {
            kafkaTemplate.send("report_requests", new ReportRequest(SubjectAverageTopicList.getClass().getName(), SubjectAverageTopicList));
            return SubjectAverageTopicList;
        } else throw new CustomException("class name not found");
    }

    @KafkaListener(
            topics = "report_requests",
            groupId = "report_requests_consumer",
            properties = {
                    "spring.json.type.mapping:ReportRequest:com.reportservice.model.ReportRequest"
            }
    )
    public void listenReportRequests(ReportRequest reportRequest) {
        logger.info(reportRequest.toString());
        simpMessagingTemplate.convertAndSend("/topic/report_requests", reportRequest);
    }
}
