package com.datacollectorservice.service;

import com.datacollectorservice.dto.ChartData;
import com.datacollectorservice.exception.CustomException;
import com.datacollectorservice.model.*;
import com.datacollectorservice.model.security.User;
import com.datacollectorservice.repository.*;
import com.datacollectorservice.security.service.UserDetailsImpl;
import com.opencsv.CSVReader;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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

    @Autowired
    private UserReportRepository userReportRepository;

    @Autowired
    private UserRepository userRepository;

    public ChartData processJsonMarks(School school) {
        studentRepository.saveAll(school.getStudents());
        schoolRepository.save(school);
        ChartData chartdata = saveToChartData(school);
        logger.info("Marks got saved");
        logger.info("Sending message to Kafka topic");
        kafkaTemplate.send("student_marks", school);
        saveAndCollectClassWiseRecordCount(school);
        return chartdata;
    }

    public ChartData processCsvMarks(MultipartFile file) throws CustomException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext(); // Read the header row
            String[] line;

            School school = new School();
            List<Student> students = new ArrayList<>();

            checkAssignedSchool(reader);

            while ((line = reader.readNext()) != null) {
                school.setSchoolName(line[0]);

                Student student = new Student();
                student.setSchoolName(line[0]);
                student.setSession(line[1]);
                student.setStudentName(line[3]);
                student.setClassName(line[2]);

                List<Marks> marksList = new ArrayList<>();
                for (int i = 4; i < line.length - 1; i += 2) {
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

            saveAndCollectClassWiseRecordCount(school);

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

    public List<Student> getAllStudentsFromSchool(String schoolName) {
        return studentRepository.findStudentsBySchoolNameIgnoreCase(schoolName);
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

    public List<SchoolAverage> getSchoolAverages() {
        return schoolAverageRepository.findAll();
    }

    private void saveUserReport(String schoolName, String className, Integer count) {
        UserReport userReport = new UserReport();
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userReport.setUsername(userDetails.getUsername());
        userReport.setSchoolName(schoolName);
        userReport.setClassName(className);
        userReport.setRecordCount(count);
        userReportRepository.save(userReport);
    }

    private void saveAndCollectClassWiseRecordCount(School school) {
        Map<String, Integer> classWiseRecordCount = new HashMap<>();

        for (Student student : school.getStudents()) {
            if (classWiseRecordCount.get(student.getClassName()) != null) {
                classWiseRecordCount.put(student.getClassName(), classWiseRecordCount.get(student.getClassName()) + 1);
            } else
                classWiseRecordCount.put(student.getClassName(), 1);
        }

        for (Map.Entry<String, Integer> entry : classWiseRecordCount.entrySet()) {
            String className = entry.getKey();
            Integer count = entry.getValue();

            saveUserReport(school.getSchoolName(), className, count);
        }
    }

    public ChartData saveToChartData(School school) {
        ChartData chartData = new ChartData();
        chartData.setStudentCount(school.getStudents().size());
        return chartRepository.save(chartData);
    }

    public List<UserReport> getAllUserReportByUsername() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());

        return userReportRepository.findAllByUsernameIs(user.get().getUsername());
    }

    public Set<String> getSchools() {
        List<School> schools = schoolRepository.findAllByOrderBySchoolName();
        Set<String> schoolSet = new HashSet<>();
        for (School school : schools) {
            schoolSet.add(school.getSchoolName());
        }
        return schoolSet;
    }

    public List<String> getSchoolOfLoggedInUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());

        return user.get().getAssignedSchool();
    }

    private void checkAssignedSchool(CSVReader reader) throws IOException, CustomException {
        String[] firstLine;
        firstLine = reader.peek();
        String schoolName = firstLine[0];
        boolean isOk = false;

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());

        for (String assignedSchool : user.get().getAssignedSchool()) {
            if (assignedSchool.equalsIgnoreCase(schoolName)) {
                isOk = true;
                break;
            }
        }

        if (!isOk) throw new CustomException("not allowed to add school");
    }

    public List<Student> getStudentsForStudentDashboard(String id) {
        List<Student> students = studentRepository.findAll();

        Optional<Student> foundStudent = students.stream()
                .filter(student -> student.getId().equals(id))
                .findFirst();

        logger.info(foundStudent.get().toString());

        Student topper = new Student();

        double maxMarks = 0;

        for (Student student : students) {
            String className = student.getClassName();
            double marks = student.getMarks().stream().mapToDouble(Marks::getMarks).sum();

            if (className.equalsIgnoreCase(foundStudent.get().getClassName()) && marks > maxMarks) {
                topper = student;
                maxMarks = marks;
            }
        }

        students.clear();

        students.add(foundStudent.get());
        students.add(topper);

        return students;
    }

    public Map<String, Student> getClassWiseToppersFromSchool(String schoolName){
        List<Student> students = studentRepository.findStudentsBySchoolNameIgnoreCase(schoolName);

        Map<String, Student> maxMarksPerClass = new HashMap<>();
        double marks, maxMarks;


        for (Student student : students) {
            String className = student.getClassName();
            marks = student.getMarks().stream().mapToDouble(Marks::getMarks).sum();

            if (maxMarksPerClass.containsKey(className)) {
                maxMarks = maxMarksPerClass.get(className).getMarks().stream().mapToDouble(Marks::getMarks).sum();
                if (marks > maxMarks) {
                    maxMarksPerClass.put(className, student);
                }
            } else {
                maxMarksPerClass.put(className, student);
            }
        }

        return maxMarksPerClass;
    }
}