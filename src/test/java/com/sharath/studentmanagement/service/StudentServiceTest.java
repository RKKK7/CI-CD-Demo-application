package com.sharath.studentmanagement.service;

import com.sharath.studentmanagement.exception.StudentNotFoundException;
import com.sharath.studentmanagement.model.Student;
import com.sharath.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @InjectMocks
    private StudentService service;

    private Student student;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .department("CSE")
                .build();
    }

    @Test
    @DisplayName("findAll returns list of students")
    void findAll_returnsList() {
        given(repository.findAll()).willReturn(List.of(student));

        List<Student> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("findById returns student when found")
    void findById_found() {
        given(repository.findById(1L)).willReturn(Optional.of(student));

        Student result = service.findById(1L);

        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("findById throws when not found")
    void findById_notFound() {
        given(repository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create saves and returns student")
    void create_success() {
        given(repository.existsByEmail("john@example.com")).willReturn(false);
        given(repository.save(any(Student.class))).willReturn(student);

        Student result = service.create(student);

        assertThat(result.getId()).isEqualTo(1L);
        verify(repository).save(student);
    }

    @Test
    @DisplayName("create throws on duplicate email")
    void create_duplicateEmail() {
        given(repository.existsByEmail("john@example.com")).willReturn(true);

        assertThatThrownBy(() -> service.create(student))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update modifies existing student")
    void update_success() {
        Student updated = Student.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .department("ISE")
                .build();

        given(repository.findById(1L)).willReturn(Optional.of(student));
        given(repository.save(any(Student.class))).willAnswer(inv -> inv.getArgument(0));

        Student result = service.update(1L, updated);

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getDepartment()).isEqualTo("ISE");
    }

    @Test
    @DisplayName("delete removes existing student")
    void delete_success() {
        given(repository.findById(1L)).willReturn(Optional.of(student));
        doNothing().when(repository).delete(student);

        service.delete(1L);

        verify(repository).delete(student);
    }

    @Test
    @DisplayName("delete throws when student not found")
    void delete_notFound() {
        given(repository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(StudentNotFoundException.class);
    }
}
