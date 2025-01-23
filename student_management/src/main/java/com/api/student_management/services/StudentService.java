package com.api.student_management.services;

import com.api.student_management.entities.Student;
import com.api.student_management.repositories.StudentRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


@Service
public class StudentService {

    private final StudentRepository studentRepository;

    private static final String CSV_FILE_LOCATION = "C:/var/log/applications/API/dataprocessing/students.xls";
    private static final String CSV_SAVE_LOCATION = "C:/var/log/applications/API/dataprocessing/students.csv";

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void generateStudentExcelSheet(
            int count) throws IOException {

        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("StudentId");
            headerRow.createCell(1).setCellValue("firstName");
            headerRow.createCell(2).setCellValue("lastName");
            headerRow.createCell(3).setCellValue("DOB");
            headerRow.createCell(4).setCellValue("studentClass");
            headerRow.createCell(5).setCellValue("score");
            headerRow.createCell(6).setCellValue("status");
            headerRow.createCell(7).setCellValue("photoPath");

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper creationHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            int dataRowIndex = 1;
            for (int i = 1; i <= count; i++) {
                Student student = new Student();
                Row row = sheet.createRow(dataRowIndex++);
                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue(student.getFirstName());
                row.createCell(2).setCellValue(student.getLastName());

                Cell dateCell = row.createCell(3);
                dateCell.setCellValue(student.getDOB().toLocalDate());
                dateCell.setCellStyle(dateCellStyle);

                row.createCell(4).setCellValue(student.getStudentClass());
                row.createCell(5).setCellValue(student.getScore());
                row.createCell(6).setCellValue(student.getStatus());
                row.createCell(7).setCellValue(student.getPhotoPath());
            }
            logger.info("Excel file generated: {}", CSV_FILE_LOCATION);
            logger.info("dataRowIndex: {}", count);
            logger.info("dataRowIndex: {}", dataRowIndex);
            try (FileOutputStream fileOut = new FileOutputStream(CSV_FILE_LOCATION)) {
                workbook.write(fileOut);
            }
            workbook.dispose();
        }
    }

    public List<Student> readExcelSheet() {
        List<Student> students = new ArrayList<>();
        Workbook workbook;

        try {
            workbook = WorkbookFactory.create(new File(CSV_FILE_LOCATION));
            workbook.forEach(sheet -> {
                logger.info("Title of sheet => {}", sheet.getSheetName());


                DataFormatter dataFormatter = new DataFormatter();
                int index = 0;
                for (Row row : sheet) {
                    if (index++ == 0)
                        continue;
                    Student student = new Student();

                    if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.NUMERIC) {
                        student.setStudentId((long) row.getCell(0).getNumericCellValue());
                    }

                    if (row.getCell(1) != null) {
                        student.setFirstName(dataFormatter.formatCellValue(row.getCell(1)));
                    }
                    if (row.getCell(2) != null) {
                        student.setLastName(dataFormatter.formatCellValue(row.getCell(2)));
                    }
                    Cell dateCell = row.getCell(3);
                    if (DateUtil.isCellDateFormatted(dateCell)) {
                        LocalDate date = dateCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        student.setDOB(date.atStartOfDay());
                    }
                    if (row.getCell(4) != null) {
                        student.setStudentClass(dataFormatter.formatCellValue(row.getCell(4)));
                    }
                    if (row.getCell(5) != null) {
                        student.setScore(Integer.valueOf(dataFormatter.formatCellValue(row.getCell(5))));
                    }
                    if (row.getCell(6) != null) {
                        student.setStatus(Integer.valueOf(dataFormatter.formatCellValue(row.getCell(6))));
                    }
                    if (row.getCell(7) != null) {
                        student.setPhotoPath(dataFormatter.formatCellValue(row.getCell(7)));
                    }
                    students.add(student);
                }
            });
            workbook.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return students;
    }

    public void saveStudentsToCsv(List<Student> students) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_SAVE_LOCATION))) {
            // Write the header row
            writer.write("StudentId,FirstName,LastName,DOB,StudentClass,Score,Status,PhotoPath");
            writer.newLine();

            // Write each student's details
            for (Student student : students) {
                writer.write(
                        String.format(
                                "%d,%s,%s,%s,%s,%d,%d,%s",
                                student.getStudentId(),
                                student.getFirstName(),
                                student.getLastName(),
                                student.getDOB() != null ? student.getDOB().toLocalDate().toString() : "",
                                student.getStudentClass(),
                                student.getScore() + 10,
                                student.getStatus(),
                                student.getPhotoPath()
                        )
                );
                writer.newLine();
            }
            writer.close();
            System.out.println("Students saved to CSV file: " + CSV_SAVE_LOCATION);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to CSV file", e);
        }
    }

    @Transactional
    public Student saveDraft(Student draftStudent, Integer makerUserId) {
        Student existingStudent = studentRepository.findById(draftStudent.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        existingStudent.setDraftFirstName(draftStudent.getDraftFirstName());
        existingStudent.setDraftLastName(draftStudent.getDraftLastName());
        existingStudent.setDraftDOB(draftStudent.getDraftDOB());
        existingStudent.setDraftStudentClass(draftStudent.getDraftStudentClass());
        existingStudent.setDraftScore(draftStudent.getDraftScore());
        existingStudent.setDraftPhotoPath(draftStudent.getDraftPhotoPath());
        existingStudent.setEditingStatus(1);
        existingStudent.setMakerUserId(makerUserId);

        return studentRepository.save(existingStudent);
    }

    @Transactional
    public Student approveChanges(Long studentId, Integer checkerUserId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getMakerUserId().equals(checkerUserId)) {
            throw new RuntimeException("Maker cannot approve their own record");
        }

        student.setFirstName(student.getDraftFirstName());
        student.setLastName(student.getDraftLastName());
        student.setDOB(student.getDraftDOB());
        student.setStudentClass(student.getDraftStudentClass());
        student.setScore(student.getDraftScore());
        student.setPhotoPath(student.getDraftPhotoPath());

        student.setDraftFirstName(null);
        student.setDraftLastName(null);
        student.setDraftDOB(null);
        student.setDraftStudentClass(null);
        student.setDraftScore(null);
        student.setDraftPhotoPath(null);

        student.setEditingStatus(0);
        student.setCheckerUserId(checkerUserId);
        student.setIsApproved(true);
        student.setCheckerComments(null);

        return studentRepository.save(student);
    }

    @Transactional
    public Student rejectChanges(Long studentId, Integer checkerUserId, String comments) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getMakerUserId().equals(checkerUserId)) {
            throw new RuntimeException("Maker cannot reject their own record");
        }

        student.setCheckerUserId(checkerUserId);
        student.setEditingStatus(2);
        student.setIsApproved(false);
        student.setCheckerComments(comments);

        return studentRepository.save(student);
    }

    @Transactional
    public Student resetDraft(Long studentId, Integer makerUserId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

//        if (!student.getMakerUserId().equals(makerUserId)) {
//            throw new RuntimeException("Only the original maker can reset the draft");
//        }

        student.setDraftFirstName(null);
        student.setDraftLastName(null);
        student.setDraftDOB(null);
        student.setDraftStudentClass(null);
        student.setDraftScore(null);
        student.setDraftPhotoPath(null);

        student.setEditingStatus(null);
        student.setCheckerComments(null);

        return studentRepository.save(student);
    }


}
