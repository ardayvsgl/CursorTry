package com.example.student.repository;

import com.example.student.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Student> findByNameContainingIgnoreCase(String name);
    
    List<Student> findByAgeBetween(Integer minAge, Integer maxAge);
    
    @Query("SELECT s FROM Student s WHERE s.age >= :minAge")
    List<Student> findStudentsOlderThan(@Param("minAge") Integer minAge);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.age BETWEEN :minAge AND :maxAge")
    Long countStudentsByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
}
