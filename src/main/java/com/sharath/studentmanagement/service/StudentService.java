package com.sharath.studentmanagement.service;

import com.sharath.studentmanagement.exception.StudentNotFoundException;
import com.sharath.studentmanagement.model.Student;
import com.sharath.studentmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;

    public List<Student> findAll() {
        return repository.findAll();
    }

    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Transactional
    public Student create(Student student) {
        if (repository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + student.getEmail());
        }
        return repository.save(student);
    }

    @Transactional
    public Student update(Long id, Student updated) {
        Student existing = findById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setDepartment(updated.getDepartment());
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Student student = findById(id);
        repository.delete(student);
    }
}
