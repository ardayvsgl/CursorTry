package com.example.student;

import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudentCrudApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();

        testStudent = Student.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .age(25)
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void fullStudentLifecycle_ShouldWorkCorrectly() throws Exception {
        // 1. Create a student
        Student newStudent = Student.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .age(22)
                .address("456 Oak Ave")
                .build();

        String createResponse = mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Jane Smith")))
                .andExpect(jsonPath("$.email", is("jane.smith@example.com")))
                .andExpect(jsonPath("$.age", is(22)))
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Student createdStudent = objectMapper.readValue(createResponse, Student.class);
        Long studentId = createdStudent.getId();

        // 2. Get the student by ID
        mockMvc.perform(get("/api/students/" + studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Smith")))
                .andExpect(jsonPath("$.email", is("jane.smith@example.com")));

        // 3. Update the student
        Student updateData = Student.builder()
                .name("Jane Smith Updated")
                .email("jane.updated@example.com")
                .age(23)
                .address("789 Pine Rd")
                .build();

        mockMvc.perform(put("/api/students/" + studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Smith Updated")))
                .andExpect(jsonPath("$.email", is("jane.updated@example.com")))
                .andExpect(jsonPath("$.age", is(23)));

        // 4. Verify the update
        mockMvc.perform(get("/api/students/" + studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Smith Updated")))
                .andExpect(jsonPath("$.email", is("jane.updated@example.com")));

        // 5. Delete the student
        mockMvc.perform(delete("/api/students/" + studentId))
                .andExpect(status().isNoContent());

        // 6. Verify the student is deleted
        mockMvc.perform(get("/api/students/" + studentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllStudents_ShouldReturnEmptyList_WhenNoStudents() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllStudents_ShouldReturnAllStudents_WhenStudentsExist() throws Exception {
        // Given
        Student student1 = studentRepository.save(Student.builder()
                .name("Alice Johnson")
                .email("alice@example.com")
                .age(20)
                .build());

        Student student2 = studentRepository.save(Student.builder()
                .name("Bob Wilson")
                .email("bob@example.com")
                .age(25)
                .build());

        // When & Then
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice Johnson")))
                .andExpect(jsonPath("$[1].name", is("Bob Wilson")));
    }

    @Test
    void searchStudentsByName_ShouldReturnMatchingStudents() throws Exception {
        // Given
        studentRepository.save(Student.builder()
                .name("Alice Johnson")
                .email("alice@example.com")
                .age(20)
                .build());

        studentRepository.save(Student.builder()
                .name("Bob Wilson")
                .email("bob@example.com")
                .age(25)
                .build());

        // When & Then
        mockMvc.perform(get("/api/students/search")
                .param("name", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Alice Johnson")));
    }

    @Test
    void getStudentsByAgeRange_ShouldReturnStudentsInRange() throws Exception {
        // Given
        studentRepository.save(Student.builder()
                .name("Alice Johnson")
                .email("alice@example.com")
                .age(20)
                .build());

        studentRepository.save(Student.builder()
                .name("Bob Wilson")
                .email("bob@example.com")
                .age(25)
                .build());

        studentRepository.save(Student.builder()
                .name("Charlie Brown")
                .email("charlie@example.com")
                .age(30)
                .build());

        // When & Then
        mockMvc.perform(get("/api/students/age-range")
                .param("minAge", "20")
                .param("maxAge", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void createStudent_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        Student invalidStudent = Student.builder()
                .name("") // Invalid: empty name
                .email("invalid-email") // Invalid: not an email
                .age(-5) // Invalid: negative age
                .build();

        // When & Then
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStudent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createStudent_WithDuplicateEmail_ShouldReturnConflict() throws Exception {
        // Given
        Student existingStudent = studentRepository.save(Student.builder()
                .name("Existing Student")
                .email("duplicate@example.com")
                .age(25)
                .build());

        Student duplicateEmailStudent = Student.builder()
                .name("New Student")
                .email("duplicate@example.com") // Same email
                .age(30)
                .build();

        // When & Then
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmailStudent)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateStudent_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        Student updateData = Student.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .age(25)
                .build();

        // When & Then
        mockMvc.perform(put("/api/students/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/students/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStudentByEmail_WhenStudentExists_ShouldReturnStudent() throws Exception {
        // Given
        Student savedStudent = studentRepository.save(Student.builder()
                .name("Email Test")
                .email("emailtest@example.com")
                .age(25)
                .build());

        // When & Then
        mockMvc.perform(get("/api/students/email/emailtest@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Email Test")));
    }

    @Test
    void getStudentByEmail_WhenStudentDoesNotExist_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/students/email/nonexistent@example.com"))
                .andExpect(status().isNotFound());
    }
}
