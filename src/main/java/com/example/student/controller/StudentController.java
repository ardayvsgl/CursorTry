package com.example.student.controller;

import com.example.student.model.Student;
import com.example.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student createdStudent = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Student> getStudentByEmail(@PathVariable String email) {
        return studentService.getStudentByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id,
            @Valid @RequestBody Student studentDetails) {
        Student updatedStudent = studentService.updateStudent(id, studentDetails);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudentsByName(@RequestParam String name) {
        List<Student> students = studentService.searchStudentsByName(name);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/age-range")
    public ResponseEntity<List<Student>> getStudentsByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        List<Student> students = studentService.getStudentsByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/older-than")
    public ResponseEntity<List<Student>> getStudentsOlderThan(@RequestParam Integer minAge) {
        List<Student> students = studentService.getStudentsOlderThan(minAge);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/count/age-range")
    public ResponseEntity<Long> countStudentsByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        Long count = studentService.countStudentsByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(count);
    }
}
