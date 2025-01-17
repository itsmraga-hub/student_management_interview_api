package com.api.student_management.services;

import com.api.student_management.controllers.StudentsController;
import com.api.student_management.entities.Student;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    private static final String CSV_FILE_LOCATION = "C:/Users/Admin/here/students.xls";
    private static final String CSV_SAVE_LOCATION = "C:/Users/Admin/here/students.csv";


    private static final Logger logger = (Logger) LoggerFactory.getLogger(StudentService.class);

    public void generateStudentExcelSheet(
            HttpServletResponse response,
            List<Student> students) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        // Create a blank sheet
        HSSFSheet sheet = workbook.createSheet("Students");
        // Create a row and put some cells in it. (Excel rows start from 0)
        HSSFRow row = sheet.createRow(0);
        // Create a cell and put a value in it. (Excel column index starts from 0)
        HSSFCell cell = row.createCell(0);
        row.createCell(0).setCellValue("StudentId");
        row.createCell(1).setCellValue("firstName");
        row.createCell(2).setCellValue("lastName");
        row.createCell(3).setCellValue("DOB");
        row.createCell(4).setCellValue("studentClass");
        row.createCell(5).setCellValue("score");
        row.createCell(6).setCellValue("status");
        row.createCell(7).setCellValue("photoPath");

        HSSFCellStyle dateCellStyle = workbook.createCellStyle();
        HSSFDataFormat dateFormat = workbook.createDataFormat();
        dateCellStyle.setDataFormat(dateFormat.getFormat("dd-mm-yyyy"));

        int dataRowIndex = 1;
        for (Student student : students) {
            row = sheet.createRow(dataRowIndex);
            row.createCell(0).setCellValue(dataRowIndex);
            row.createCell(1).setCellValue(student.getFirstName());
            row.createCell(2).setCellValue(student.getLastName());
            cell = row.createCell(3);
            cell.setCellValue(student.getDOB().toLocalDate());
            cell.setCellStyle(dateCellStyle);
            row.createCell(4).setCellValue(student.getStudentClass());
            row.createCell(5).setCellValue(student.getScore());
            row.createCell(6).setCellValue(student.getStatus());
            row.createCell(7).setCellValue(student.getPhotoPath());
            dataRowIndex++;
        }

        try (FileOutputStream fileOut = new FileOutputStream(CSV_FILE_LOCATION)) {
            workbook.write(fileOut);
        }
//        ServletOutputStream ops = response.getOutputStream();
//        workbook.write(ops);
        workbook.close();
//        ops.close();
    }

    public List<Student> readExcelSheet() {
        List<Student> students = new ArrayList<>();
        Workbook workbook = null;

        try {
            workbook = WorkbookFactory.create(new File(CSV_FILE_LOCATION));

            // Retrieving the number of sheets in the Workbook
//            logger.info("Number of sheets: " + workbook.getNumberOfSheets());

            // Print all sheets name
            workbook.forEach(sheet -> {
                logger.info("Title of sheet => " + sheet.getSheetName());


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
//                    if (row.getCell(3) != null) {
//                        student.setDOB(LocalDateTime.parse(dataFormatter.formatCellValue(row.getCell(3))));
//                    }
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

//                    Cell dateCell = row.getCell(2);
//                    if (DateUtil.isCellDateFormatted(dateCell)) {
//                        LocalDate date = dateCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault())
//                                .toLocalDate();
//                        student.setDOB(date);
//                    }

//                    if (row.getCell(3) != null && row.getCell(3).getCellType() == CellType.NUMERIC) {
//                        student.setNumber((int) row.getCell(3).getNumericCellValue());
//                    }
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
}
