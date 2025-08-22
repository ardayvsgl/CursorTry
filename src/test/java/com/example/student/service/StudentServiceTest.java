package com.example.student.service;

import com.example.student.exception.DuplicateEmailException;
import com.example.student.exception.StudentNotFoundException;
import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private Student testStudent2;

    @BeforeEach
    void setUp() {
        testStudent = Student.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .age(25)
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testStudent2 = Student.builder()
                .id(2L)
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .age(22)
                .address("456 Oak Ave")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllStudents_ShouldReturnAllStudents() {
        // Given
        List<Student> expectedStudents = Arrays.asList(testStudent, testStudent2);
        when(studentRepository.findAll()).thenReturn(expectedStudents);

        // When
        List<Student> actualStudents = studentService.getAllStudents();

        // Then
        assertEquals(expectedStudents, actualStudents);
        verify(studentRepository).findAll();
    }

    @Test
    void getStudentById_WhenStudentExists_ShouldReturnStudent() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        // When
        Student actualStudent = studentService.getStudentById(1L);

        // Then
        assertEquals(testStudent, actualStudent);
        verify(studentRepository).findById(1L);
    }

    @Test
    void getStudentById_WhenStudentDoesNotExist_ShouldThrowException() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentById(999L));
        verify(studentRepository).findById(999L);
    }

    @Test
    void getStudentByEmail_WhenStudentExists_ShouldReturnStudent() {
        // Given
        when(studentRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testStudent));

        // When
        Optional<Student> actualStudent = studentService.getStudentByEmail("john.doe@example.com");

        // Then
        assertTrue(actualStudent.isPresent());
        assertEquals(testStudent, actualStudent.get());
        verify(studentRepository).findByEmail("john.doe@example.com");
    }

    @Test
    void getStudentByEmail_WhenStudentDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(studentRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        Optional<Student> actualStudent = studentService.getStudentByEmail("nonexistent@example.com");

        // Then
        assertFalse(actualStudent.isPresent());
        verify(studentRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void createStudent_WhenEmailIsUnique_ShouldCreateStudent() {
        // Given
        Student newStudent = Student.builder()
                .name("New Student")
                .email("new@example.com")
                .age(20)
                .build();

        when(studentRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(newStudent);

        // When
        Student createdStudent = studentService.createStudent(newStudent);

        // Then
        assertNotNull(createdStudent);
        assertEquals("New Student", createdStudent.getName());
        verify(studentRepository).existsByEmail("new@example.com");
        verify(studentRepository).save(newStudent);
    }

    @Test
    void createStudent_WhenEmailAlreadyExists_ShouldThrowException() {
        // Given
        Student newStudent = Student.builder()
                .name("New Student")
                .email("john.doe@example.com")
                .age(20)
                .build();

        when(studentRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When & Then
        assertThrows(DuplicateEmailException.class, () -> studentService.createStudent(newStudent));
        verify(studentRepository).existsByEmail("john.doe@example.com");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void updateStudent_WhenStudentExistsAndEmailIsUnique_ShouldUpdateStudent() {
        // Given
        Student updateData = Student.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .age(26)
                .address("Updated Address")
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // When
        Student updatedStudent = studentService.updateStudent(1L, updateData);

        // Then
        assertNotNull(updatedStudent);
        verify(studentRepository).findById(1L);
        verify(studentRepository).existsByEmail("updated@example.com");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void updateStudent_WhenStudentDoesNotExist_ShouldThrowException() {
        // Given
        Student updateData = Student.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .age(26)
                .build();

        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(StudentNotFoundException.class, () -> studentService.updateStudent(999L, updateData));
        verify(studentRepository).findById(999L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void updateStudent_WhenEmailAlreadyExists_ShouldThrowException() {
        // Given
        Student updateData = Student.builder()
                .name("Updated Name")
                .email("jane.smith@example.com")
                .age(26)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.existsByEmail("jane.smith@example.com")).thenReturn(true);

        // When & Then
        assertThrows(DuplicateEmailException.class, () -> studentService.updateStudent(1L, updateData));
        verify(studentRepository).findById(1L);
        verify(studentRepository).existsByEmail("jane.smith@example.com");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void deleteStudent_WhenStudentExists_ShouldDeleteStudent() {
        // Given
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        // When
        studentService.deleteStudent(1L);

        // Then
        verify(studentRepository).existsById(1L);
        verify(studentRepository).deleteById(1L);
    }

    @Test
    void deleteStudent_WhenStudentDoesNotExist_ShouldThrowException() {
        // Given
        when(studentRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(StudentNotFoundException.class, () -> studentService.deleteStudent(999L));
        verify(studentRepository).existsById(999L);
        verify(studentRepository, never()).deleteById(anyLong());
    }

    @Test
    void searchStudentsByName_ShouldReturnMatchingStudents() {
        // Given
        List<Student> expectedStudents = Arrays.asList(testStudent);
        when(studentRepository.findByNameContainingIgnoreCase("John")).thenReturn(expectedStudents);

        // When
        List<Student> actualStudents = studentService.searchStudentsByName("John");

        // Then
        assertEquals(expectedStudents, actualStudents);
        verify(studentRepository).findByNameContainingIgnoreCase("John");
    }

    @Test
    void getStudentsByAgeRange_ShouldReturnStudentsInRange() {
        // Given
        List<Student> expectedStudents = Arrays.asList(testStudent, testStudent2);
        when(studentRepository.findByAgeBetween(20, 30)).thenReturn(expectedStudents);

        // When
        List<Student> actualStudents = studentService.getStudentsByAgeRange(20, 30);

        // Then
        assertEquals(expectedStudents, actualStudents);
        verify(studentRepository).findByAgeBetween(20, 30);
    }

    @Test
    void getStudentsOlderThan_ShouldReturnOlderStudents() {
        // Given
        List<Student> expectedStudents = Arrays.asList(testStudent);
        when(studentRepository.findStudentsOlderThan(20)).thenReturn(expectedStudents);

        // When
        List<Student> actualStudents = studentService.getStudentsOlderThan(20);

        // Then
        assertEquals(expectedStudents, actualStudents);
        verify(studentRepository).findStudentsOlderThan(20);
    }

    @Test
    void countStudentsByAgeRange_ShouldReturnCorrectCount() {
        // Given
        when(studentRepository.countStudentsByAgeRange(20, 30)).thenReturn(2L);

        // When
        Long count = studentService.countStudentsByAgeRange(20, 30);

        // Then
        assertEquals(2L, count);
        verify(studentRepository).countStudentsByAgeRange(20, 30);
    }
}
