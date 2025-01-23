package com.api.student_management.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "students")
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long studentId;

    @Column(nullable = false, length = 8)
    private String firstName;
    @Column(nullable = false, length = 8)
    private String lastName;
    private LocalDateTime DOB;
    private String studentClass;

    private Integer score;
    private Integer status = 1;
    private String photoPath = "";

    private Integer editingStatus;
    private Integer makerUserId;
    private Integer checkerUserId;
    private String checkerComments;
    private Boolean isApproved;

    private String draftFirstName;
    private String draftLastName;
    private LocalDateTime draftDOB;
    private String draftStudentClass;
    private Integer draftScore;
    private String draftPhotoPath;

//    public Student() {
//    }

    public Student() {
        this.firstName = generateRandomString();
        this.lastName = generateRandomString();
        this.DOB = generateRandomDOB().atStartOfDay();
        this.studentClass = generateRandomClass();
        this.score = generateRandomScore(55, 85);
        this.status = 1;
        this.photoPath = "";
    }

    private String generateRandomString() {
        int length = ThreadLocalRandom.current().nextInt(3, 8 + 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('a' + ThreadLocalRandom.current().nextInt(0, 26));
            sb.append(randomChar);
        }
        return sb.toString();
    }

    private LocalDate generateRandomDOB() {
        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2010, 12, 31);
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        long randomDays = ThreadLocalRandom.current().nextLong(0, daysBetween + 1);
        return startDate.plusDays(randomDays);
    }

    private String generateRandomClass() {
        String[] classes = {"Class 1", "Class 2", "Class 3", "Class 4", "Class 5"};
        int randomIndex = ThreadLocalRandom.current().nextInt(classes.length);
        return classes[randomIndex];
    }

    private int generateRandomScore(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    // getters and setters...
    public Long getStudentId() {
        return studentId;
    }
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public LocalDateTime getDOB() {
        return DOB;
    }
    public void setDOB(LocalDateTime DOB) {
        this.DOB = DOB;
    }
    public String getStudentClass() {
        return studentClass;
    }
    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }
    public Integer getScore() {
        return score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getPhotoPath() {
        return photoPath;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Integer getEditingStatus() {
        return editingStatus;
    }
    public void setEditingStatus(Integer editingStatus) {
        this.editingStatus = editingStatus;
    }

    public Integer getMakerUserId() {
        return makerUserId;
    }
    public void setMakerUserId(Integer makerUserId) {
        this.makerUserId = makerUserId;
    }

    public Integer getCheckerUserId() {
        return checkerUserId;
    }
    public void setCheckerUserId(Integer checkerUserId) {
        this.checkerUserId = checkerUserId;
    }

    public String getCheckerComments() {
        return checkerComments;
    }
    public void setCheckerComments(String checkerComments) {
        this.checkerComments = checkerComments;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public String getDraftFirstName() {
        return draftFirstName;
    }

    public void setDraftFirstName(String draftFirstName) {
        this.draftFirstName = draftFirstName;
    }

    public String getDraftLastName() {
        return draftLastName;
    }

    public void setDraftLastName(String draftLastName) {
        this.draftLastName = draftLastName;
    }

    public LocalDateTime getDraftDOB() {
        return draftDOB;
    }

    public void setDraftDOB(LocalDateTime draftDOB) {
        this.draftDOB = draftDOB;
    }

    public String getDraftStudentClass() {
        return draftStudentClass;
    }

    public void setDraftStudentClass(String draftStudentClass) {
        this.draftStudentClass = draftStudentClass;
    }

    public Integer getDraftScore() {
        return draftScore;
    }

    public void setDraftScore(Integer draftScore) {
        this.draftScore = draftScore;
    }

    public String getDraftPhotoPath() {
        return draftPhotoPath;
    }

    public void setDraftPhotoPath(String draftPhotoPath) {
        this.draftPhotoPath = draftPhotoPath;
    }


    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", DOB=" + DOB +
                ", className='" + studentClass + '\'' +
                ", score=" + score +
                ", status=" + status +
                ", photoPath='" + photoPath + '\'' +
                '}';
    }

}
