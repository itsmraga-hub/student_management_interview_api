package com.api.student_management.repositories;

import com.api.student_management.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // Additional custom methods for student management

    List<Student> findByStatus(Integer status);


}
