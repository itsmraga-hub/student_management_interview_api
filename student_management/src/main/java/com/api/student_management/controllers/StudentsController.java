package com.api.student_management.controllers;

import com.api.student_management.entities.Student;
import com.api.student_management.repositories.StudentRepository;
import com.api.student_management.services.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/students")
@RestController
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class StudentsController {

    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(StudentsController.class);

    public StudentsController(StudentRepository studentRepository, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    @GetMapping("/")
    public ResponseEntity getStudents() {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    @PostMapping("/")
    public String addStudent() {
        return "Student added";
    }

    @PostMapping("/generate")
//    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity generateStudents(
            HttpServletResponse response,
            @RequestBody() int count
    ) throws IOException {
        response.setContentType("application/octet-stream");

        // Excel file will be generated and saved to C:\Users\admin\Downloads as
        // 'employee.xls'

        String headerKey = "Content-Disposition";
        String headerValue = "attachment;filename=students.xlsx";
        response.setHeader(headerKey, headerValue);
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Student student = new Student();
            logger.info("Generated student: {}", student);
            students.add(student);
        }
        studentService.generateStudentExcelSheet(response, students);
        return ResponseEntity.ok("Generated " + count + " students");
    }

    @GetMapping("/read")

    public @ResponseBody List<Student> readExcelSheet() {
        logger.info("Read courses in Excel file and return courses in JSON format");
        return studentService.readExcelSheet();
    }


    @PostMapping("/csv/save")
    public  ResponseEntity saveToCSVFile() {
        logger.info("Saving students to CSV file");
        List<Student> students = studentService.readExcelSheet();
        studentService.saveStudentsToCsv(students);
        return ResponseEntity.ok("Students saved to CSV file");
    }


    @PostMapping("/db/save")
    public  ResponseEntity saveToDatabase() {
        logger.info("Saving students to CSV file");
        List<Student> excelStudents = studentService.readExcelSheet();
        List<Student> students = new ArrayList<>();
        logger.info("Saved students to CSV file");
        logger.info("Updating student scores");
        logger.info("students {}", students.size());
        logger.info("students {}", students.size());
        for (Student student : excelStudents) {
            student.setStudentId(null);
            student.setScore(student.getScore() + 5);
            students.add(student);
        }

        studentRepository.saveAll(students);
//        studentService.saveStudentsToCsv(students);
        return ResponseEntity.ok("Students saved to Database");
    }

}
