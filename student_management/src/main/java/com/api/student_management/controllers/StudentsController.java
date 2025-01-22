package com.api.student_management.controllers;

import com.api.student_management.dtos.UploadPhotoResponseDTO;
import com.api.student_management.entities.Student;
import com.api.student_management.repositories.StudentRepository;
import com.api.student_management.services.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequestMapping("/students")
@RestController
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class StudentsController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(StudentsController.class);

    public StudentsController(StudentRepository studentRepository, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    @GetMapping("")
    public ResponseEntity<List<Student>> getStudents() {
        return ResponseEntity.ok(studentRepository.findByStatus(1));
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Student>> getInactiveStudents() {
        return ResponseEntity.ok(studentRepository.findByStatus(0));
    }

    @PostMapping()
    public String addStudent() {
        return "Student added";
    }

//    @PostMapping("/generate")
//    public ResponseEntity<Map<String, String>> generateStudents(
//            HttpServletResponse response,
//            @RequestParam() int count
//    ) throws IOException {
//        studentService.generateStudentExcelSheet(response, count);
//        Map<String, String> responseMap = Map.of("message", "Generated " + count + " students");
//        return ResponseEntity.ok(responseMap);
//    }

    @PostMapping("/gen")
    public CompletableFuture<ResponseEntity<Map<String, String>>> generateStudents(
            @RequestParam() int count
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Generating students");
                studentService.generateStudentExcelSheet(count);
                logger.info("Students generated");
                // If everything is successful, return the success message
                return ResponseEntity.ok(Map.of("message", "Generated " + count + " students"));
            } catch (IOException e) {
                logger.error("Error generating student Excel sheet", e);
                // Return an error response if something goes wrong
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Error generating student Excel sheet"));
            }
        });
    }


    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateStudents2(
            @RequestParam() int count
    ) throws IOException {
        studentService.generateStudentExcelSheet(count);
        logger.info("Students generated");
        // If everything is successful, return the success message
        return ResponseEntity.ok(Map.of("message", "Generated " + count + " students"));
    }



    @GetMapping("/excel")

    public @ResponseBody List<Student> readExcelSheet() {
        return studentService.readExcelSheet();
    }

    @GetMapping("/csv")

    public @ResponseBody List<Student> readCSV() {
        return studentService.readExcelSheet();
    }

    @GetMapping("/sql")

    public @ResponseBody List<Student> readMySQL() {
        return studentRepository.findByStatus(1);
    }

    @GetMapping("/sql/{id}")

    public @ResponseBody Student readMySQLStudent(@PathVariable Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/")
    public ResponseEntity<String> deleteStudents() {
        studentRepository.deleteAll();
        return ResponseEntity.ok("Students deleted");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return ResponseEntity.ok(Map.of("message", "Student not found"));
        }
        student.setStatus(0);
        studentRepository.save(student);
        return ResponseEntity.ok(Map.of("message", "Student deleted"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        logger.info("Updating student");
        logger.info("Student: {}", student);

        Student studentToUpdate = studentRepository.findById(id).orElse(null);
        if (studentToUpdate == null) {
            return ResponseEntity.ok(student);
        }
        studentToUpdate.setFirstName(student.getFirstName());
        studentToUpdate.setLastName(student.getLastName());
        studentToUpdate.setScore(student.getScore());
        studentToUpdate.setPhotoPath(student.getPhotoPath());
        studentToUpdate.setDOB(student.getDOB());
        studentToUpdate.setStudentClass(student.getStudentClass());
//        studentToUpdate.setStatus(1);

        studentRepository.save(studentToUpdate);

        return ResponseEntity.ok(student);
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<UploadPhotoResponseDTO> updateStudentPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {


        Student studentToUpdate = studentRepository.findById(id).orElse(null);
        if (studentToUpdate == null) {
            return ResponseEntity.ok(new UploadPhotoResponseDTO("Student not found", null));
        }
        try {
            String filePath = saveImage(file, id.toString());
            return ResponseEntity.ok(new UploadPhotoResponseDTO("Image uploaded successfully", filePath));
        } catch (IOException e) {
            ResponseEntity.ok(new UploadPhotoResponseDTO("Error uploading image", null));
        }
        return ResponseEntity.ok(new UploadPhotoResponseDTO("Failed to upload Photo", null));
    }


    @PostMapping("/csv/save")
    public  ResponseEntity<Map<String, String>> saveToCSVFile() {
        List<Student> students = studentService.readExcelSheet();
        studentService.saveStudentsToCsv(students);
        return ResponseEntity.ok(Map.of("message", students.size() + " students saved to CSV file"));
    }


    @PostMapping("/db/save")
    public  ResponseEntity<Map<String, String>> saveToDatabase() {
        logger.info("Saving students to database");
        List<Student> excelStudents = studentService.readExcelSheet();
        List<Student> students = new ArrayList<>();
        for (Student student : excelStudents) {
            student.setStudentId(null);
            student.setScore(student.getScore() + 5);
            students.add(student);
        }

        studentRepository.saveAll(students);
        return ResponseEntity.ok(Map.of("message", students.size() + " students saved to Database"));
    }

    private String saveImage(MultipartFile file, String studentId) throws IOException {
        String contentType = file.getContentType();
        assert contentType != null;
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            throw new IllegalArgumentException("Only JPEG or PNG images are allowed");
        }
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        assert fileName != null;
        Path filePath = uploadPath.resolve(studentId + "-" + fileName.replace(" ", ""));
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

}
