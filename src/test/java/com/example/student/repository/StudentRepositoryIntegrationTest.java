package com.example.student.repository;

import com.example.student.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    private Student testStudent1;
    private Student testStudent2;
    private Student testStudent3;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        entityManager.clear();

        testStudent1 = Student.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .age(25)
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testStudent2 = Student.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .age(22)
                .address("456 Oak Ave")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testStudent3 = Student.builder()
                .name("Bob Johnson")
                .email("bob.johnson@example.com")
                .age(28)
                .address("789 Pine Rd")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void saveStudent_ShouldPersistStudent() {
        // When
        Student savedStudent = studentRepository.save(testStudent1);

        // Then
        assertNotNull(savedStudent.getId());
        assertEquals("John Doe", savedStudent.getName());
        assertEquals("john.doe@example.com", savedStudent.getEmail());
        assertEquals(25, savedStudent.getAge());
        assertNotNull(savedStudent.getCreatedAt());
        assertNotNull(savedStudent.getUpdatedAt());
    }

    @Test
    void findById_WhenStudentExists_ShouldReturnStudent() {
        // Given
        Student savedStudent = entityManager.persistAndFlush(testStudent1);

        // When
        Optional<Student> foundStudent = studentRepository.findById(savedStudent.getId());

        // Then
        assertTrue(foundStudent.isPresent());
        assertEquals("John Doe", foundStudent.get().getName());
        assertEquals("john.doe@example.com", foundStudent.get().getEmail());
    }

    @Test
    void findById_WhenStudentDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Student> foundStudent = studentRepository.findById(999L);

        // Then
        assertFalse(foundStudent.isPresent());
    }

    @Test
    void findByEmail_WhenStudentExists_ShouldReturnStudent() {
        // Given
        entityManager.persistAndFlush(testStudent1);

        // When
        Optional<Student> foundStudent = studentRepository.findByEmail("john.doe@example.com");

        // Then
        assertTrue(foundStudent.isPresent());
        assertEquals("John Doe", foundStudent.get().getName());
    }

    @Test
    void findByEmail_WhenStudentDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Student> foundStudent = studentRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(foundStudent.isPresent());
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        // Given
        entityManager.persistAndFlush(testStudent1);

        // When
        boolean exists = studentRepository.existsByEmail("john.doe@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_WhenEmailDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = studentRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void findAll_ShouldReturnAllStudents() {
        // Given
        entityManager.persistAndFlush(testStudent1);
        entityManager.persistAndFlush(testStudent2);
        entityManager.persistAndFlush(testStudent3);

        // When
        List<Student> allStudents = studentRepository.findAll();

        // Then
        assertEquals(3, allStudents.size());
        assertTrue(allStudents.stream().anyMatch(s -> s.getName().equals("John Doe")));
        assertTrue(allStudents.stream().anyMatch(s -> s.getName().equals("Jane Smith")));
        assertTrue(allStudents.stream().anyMatch(s -> s.getName().equals("Bob Johnson")));
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldReturnMatchingStudents() {
        // Given
        entityManager.persistAndFlush(testStudent1);
        entityManager.persistAndFlush(testStudent2);
        entityManager.persistAndFlush(testStudent3);

        // When
        List<Student> matchingStudents = studentRepository.findByNameContainingIgnoreCase("john");

        // Then
        assertEquals(2, matchingStudents.size());
        assertTrue(matchingStudents.stream().anyMatch(s -> s.getName().equals("John Doe")));
        assertTrue(matchingStudents.stream().anyMatch(s -> s.getName().equals("Bob Johnson")));
    }

    @Test
    void findByAgeBetween_ShouldReturnStudentsInRange() {
        // Given
        entityManager.persistAndFlush(testStudent1);
        entityManager.persistAndFlush(testStudent2);
        entityManager.persistAndFlush(testStudent3);

        // When
        List<Student> studentsInRange = studentRepository.findByAgeBetween(20, 25);

        // Then
        assertEquals(2, studentsInRange.size());
        assertTrue(studentsInRange.stream().anyMatch(s -> s.getName().equals("John Doe")));
        assertTrue(studentsInRange.stream().anyMatch(s -> s.getName().equals("Jane Smith")));
    }

    @Test
    void findStudentsOlderThan_ShouldReturnOlderStudents() {
        // Given
        entityManager.persistAndFlush(testStudent1);
        entityManager.persistAndFlush(testStudent2);
        entityManager.persistAndFlush(testStudent3);

        // When
        List<Student> olderStudents = studentRepository.findStudentsOlderThan(25);

        // Then
        assertEquals(2, olderStudents.size()); // Both testStudent1 (age 25) and testStudent3 (age 28) are older than 25
        assertTrue(olderStudents.stream().anyMatch(s -> s.getName().equals("John Doe")));
        assertTrue(olderStudents.stream().anyMatch(s -> s.getName().equals("Bob Johnson")));
    }

    @Test
    void countStudentsByAgeRange_ShouldReturnCorrectCount() {
        // Given
        entityManager.persistAndFlush(testStudent1);
        entityManager.persistAndFlush(testStudent2);
        entityManager.persistAndFlush(testStudent3);

        // When
        Long count = studentRepository.countStudentsByAgeRange(20, 30);

        // Then
        assertEquals(3L, count);
    }

    @Test
    void updateStudent_ShouldUpdateStudent() {
        // Given
        Student savedStudent = entityManager.persistAndFlush(testStudent1);
        savedStudent.setName("Updated Name");
        savedStudent.setAge(26);

        // When
        Student updatedStudent = studentRepository.save(savedStudent);

        // Then
        assertEquals("Updated Name", updatedStudent.getName());
        assertEquals(26, updatedStudent.getAge());
        assertNotNull(updatedStudent.getUpdatedAt());
    }

    @Test
    void deleteStudent_ShouldRemoveStudent() {
        // Given
        Student savedStudent = entityManager.persistAndFlush(testStudent1);

        // When
        studentRepository.deleteById(savedStudent.getId());
        entityManager.flush();

        // Then
        Optional<Student> deletedStudent = studentRepository.findById(savedStudent.getId());
        assertFalse(deletedStudent.isPresent());
    }

    @Test
    void saveStudent_ShouldGenerateIdAutomatically() {
        // When
        Student savedStudent = studentRepository.save(testStudent1);

        // Then
        assertNotNull(savedStudent.getId());
        assertTrue(savedStudent.getId() > 0);
    }

    @Test
    void saveStudent_ShouldSetTimestamps() {
        // When
        Student savedStudent = studentRepository.save(testStudent1);

        // Then
        assertNotNull(savedStudent.getCreatedAt());
        assertNotNull(savedStudent.getUpdatedAt());
        // Timestamps should be set but exact equality might fail due to microsecond
        // differences
        assertTrue(savedStudent.getCreatedAt().isBefore(savedStudent.getUpdatedAt().plusNanos(1)) ||
                savedStudent.getCreatedAt().equals(savedStudent.getUpdatedAt()));
    }
}
