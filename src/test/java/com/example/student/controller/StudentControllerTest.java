package com.example.student.controller;

import com.example.student.exception.DuplicateEmailException;
import com.example.student.exception.StudentNotFoundException;
import com.example.student.model.Student;
import com.example.student.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
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
        void createStudent_WhenValidStudent_ShouldReturnCreatedStudent() throws Exception {
                // Given
                Student newStudent = Student.builder()
                                .name("New Student")
                                .email("new@example.com")
                                .age(20)
                                .build();

                when(studentService.createStudent(any(Student.class))).thenReturn(newStudent);

                // When & Then
                mockMvc.perform(post("/api/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newStudent)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name", is("New Student")))
                                .andExpect(jsonPath("$.email", is("new@example.com")))
                                .andExpect(jsonPath("$.age", is(20)));

                verify(studentService).createStudent(any(Student.class));
        }

        @Test
        void createStudent_WhenInvalidStudent_ShouldReturnBadRequest() throws Exception {
                // Given
                Student invalidStudent = Student.builder()
                                .name("") 
                                .email("invalid-email") 
                                .age(-5) 
                                .build();

                // When & Then
                mockMvc.perform(post("/api/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidStudent)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void getStudentById_WhenStudentExists_ShouldReturnStudent() throws Exception {
                // Given
                when(studentService.getStudentById(1L)).thenReturn(testStudent);

                // When & Then
                mockMvc.perform(get("/api/students/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.name", is("John Doe")))
                                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                                .andExpect(jsonPath("$.age", is(25)));

                verify(studentService).getStudentById(1L);
        }

        @Test
        void getStudentById_WhenStudentDoesNotExist_ShouldReturnNotFound() throws Exception {
                // Given
                when(studentService.getStudentById(999L)).thenThrow(new StudentNotFoundException(999L));

                // When & Then
                mockMvc.perform(get("/api/students/999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", containsString("not found")));

                verify(studentService).getStudentById(999L);
        }

        @Test
        void getAllStudents_ShouldReturnAllStudents() throws Exception {
                // Given
                List<Student> students = Arrays.asList(testStudent, testStudent2);
                when(studentService.getAllStudents()).thenReturn(students);

                // When & Then
                mockMvc.perform(get("/api/students"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].name", is("John Doe")))
                                .andExpect(jsonPath("$[1].name", is("Jane Smith")));

                verify(studentService).getAllStudents();
        }

        @Test
        void getStudentByEmail_WhenStudentExists_ShouldReturnStudent() throws Exception {
                // Given
                when(studentService.getStudentByEmail("john.doe@example.com")).thenReturn(Optional.of(testStudent));

                // When & Then
                mockMvc.perform(get("/api/students/email/john.doe@example.com"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is("John Doe")));

                verify(studentService).getStudentByEmail("john.doe@example.com");
        }

        @Test
        void getStudentByEmail_WhenStudentDoesNotExist_ShouldReturnNotFound() throws Exception {
                // Given
                when(studentService.getStudentByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

                // When & Then
                mockMvc.perform(get("/api/students/email/nonexistent@example.com"))
                                .andExpect(status().isNotFound());

                verify(studentService).getStudentByEmail("nonexistent@example.com");
        }

        @Test
        void updateStudent_WhenStudentDoesNotExist_ShouldReturnNotFound() throws Exception {
                // Given
                Student updateData = Student.builder()
                                .name("Updated Name")
                                .email("updated@example.com")
                                .age(26)
                                .build();

                // Mock the service to throw exception for non-existent student
                when(studentService.updateStudent(eq(999L), any(Student.class)))
                                .thenThrow(new StudentNotFoundException(999L));

                // When & Then
                mockMvc.perform(put("/api/students/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateData)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", containsString("not found")));

                verify(studentService).updateStudent(eq(999L), any(Student.class));
        }

        @Test
        void updateStudent_WhenValidUpdate_ShouldReturnUpdatedStudent() throws Exception {
                // Given
                Student updateData = Student.builder()
                                .name("Updated Name")
                                .email("updated@example.com")
                                .age(26)
                                .address("Updated Address")
                                .build();

                Student updatedStudent = Student.builder()
                                .id(1L)
                                .name("Updated Name")
                                .email("updated@example.com")
                                .age(26)
                                .address("Updated Address")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                // Mock the service to return the updated student
                when(studentService.updateStudent(eq(1L), any(Student.class)))
                                .thenReturn(updatedStudent);

                // When & Then
                mockMvc.perform(put("/api/students/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateData)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is("Updated Name")))
                                .andExpect(jsonPath("$.email", is("updated@example.com")));

                verify(studentService).updateStudent(eq(1L), any(Student.class));
        }

        @Test
        void deleteStudent_WhenStudentExists_ShouldReturnNoContent() throws Exception {
                // Given
                doNothing().when(studentService).deleteStudent(1L);

                // When & Then
                mockMvc.perform(delete("/api/students/1"))
                                .andExpect(status().isNoContent());

                verify(studentService).deleteStudent(1L);
        }

        @Test
        void deleteStudent_WhenStudentDoesNotExist_ShouldReturnNotFound() throws Exception {
                // Given
                doThrow(new StudentNotFoundException(999L)).when(studentService).deleteStudent(999L);

                // When & Then
                mockMvc.perform(delete("/api/students/999"))
                                .andExpect(status().isNotFound());

                verify(studentService).deleteStudent(999L);
        }

        @Test
        void searchStudentsByName_ShouldReturnMatchingStudents() throws Exception {
                // Given
                List<Student> matchingStudents = Arrays.asList(testStudent);
                when(studentService.searchStudentsByName("John")).thenReturn(matchingStudents);

                // When & Then
                mockMvc.perform(get("/api/students/search")
                                .param("name", "John"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].name", is("John Doe")));

                verify(studentService).searchStudentsByName("John");
        }

        @Test
        void getStudentsByAgeRange_ShouldReturnStudentsInRange() throws Exception {
                // Given
                List<Student> studentsInRange = Arrays.asList(testStudent, testStudent2);
                when(studentService.getStudentsByAgeRange(20, 30)).thenReturn(studentsInRange);

                // When & Then
                mockMvc.perform(get("/api/students/age-range")
                                .param("minAge", "20")
                                .param("maxAge", "30"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)));

                verify(studentService).getStudentsByAgeRange(20, 30);
        }

        @Test
        void getStudentsOlderThan_ShouldReturnOlderStudents() throws Exception {
                // Given
                List<Student> olderStudents = Arrays.asList(testStudent);
                when(studentService.getStudentsOlderThan(20)).thenReturn(olderStudents);

                // When & Then
                mockMvc.perform(get("/api/students/older-than")
                                .param("minAge", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));

                verify(studentService).getStudentsOlderThan(20);
        }

        @Test
        void countStudentsByAgeRange_ShouldReturnCorrectCount() throws Exception {
                // Given
                when(studentService.countStudentsByAgeRange(20, 30)).thenReturn(2L);

                // When & Then
                mockMvc.perform(get("/api/students/count/age-range")
                                .param("minAge", "20")
                                .param("maxAge", "30"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("2"));

                verify(studentService).countStudentsByAgeRange(20, 30);
        }
}
