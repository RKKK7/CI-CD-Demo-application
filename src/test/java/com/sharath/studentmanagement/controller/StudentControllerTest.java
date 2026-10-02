package com.sharath.studentmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharath.studentmanagement.model.Student;
import com.sharath.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private StudentRepository repository;

    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    private Student sampleStudent() {
        return Student.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .department("CSE")
                .build();
    }

    @Test
    @DisplayName("POST /api/students — creates student")
    void createStudent() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sampleStudent())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    @DisplayName("GET /api/students — returns list")
    void getAllStudents() throws Exception {
        repository.save(sampleStudent());

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/students/{id} — returns single student")
    void getStudentById() throws Exception {
        Student saved = repository.save(sampleStudent());

        mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("GET /api/students/{id} — 404 when not found")
    void getStudentById_notFound() throws Exception {
        mockMvc.perform(get("/api/students/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/students/{id} — updates student")
    void updateStudent() throws Exception {
        Student saved = repository.save(sampleStudent());
        saved.setFirstName("Bob");

        mockMvc.perform(put("/api/students/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Bob"));
    }

    @Test
    @DisplayName("DELETE /api/students/{id} — deletes student")
    void deleteStudent() throws Exception {
        Student saved = repository.save(sampleStudent());

        mockMvc.perform(delete("/api/students/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/students — 400 on blank name")
    void createStudent_validationError() throws Exception {
        Student invalid = Student.builder()
                .firstName("")
                .lastName("X")
                .email("x@example.com")
                .build();

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists());
    }
}
