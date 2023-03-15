package com.example.demo.student;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents(){
        return studentRepository.findAll();
    }

    public void addNewStudent(Student student) {
        Optional<Student> studentByEmail = studentRepository.findStudentByEmail(student.getEmail());
        if(studentByEmail.isPresent()){
            throw new IllegalArgumentException("email taken");
        }
        studentRepository.save(student);
    }

    public void deleteStudent(Long id){
        boolean exists =studentRepository.existsById(id);
        if(!exists)
            throw new IllegalStateException("student with id " + id + "does not exists");
        else
            studentRepository.deleteById(id);
    }

    /**
     * variant with @Transactional
     *
     * @param studentId id
     * @param name name
     * @param email email
     */
    @Transactional
    public void updateStudent(Long studentId,String name,String email){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException("student with id " + studentId + " does not exists"));

        if(name != null &&
                name.length() > 0 &&
                !Objects.equals(student.getName(),name)){
            student.setName(name);
        }

        if(email != null &&
                email.length() > 0 &&
                !Objects.equals(student.getEmail(),email)){
            Optional<Student> studentOptional = studentRepository.findStudentByEmail(email);
            if(studentOptional.isPresent())
                throw new IllegalStateException("email is taken");
            else
                student.setEmail(email);
        }


    }

    /**
     * variant 2
     * @param studentId id
     * @param name name
     * @param email email
     */
    public void update2Student(Long studentId, String name, String email) {

        studentRepository.findById(studentId)
                .map(student -> {
                    student.setName(name);
                    student.setEmail(email);
                    return studentRepository.save(student);
            });
    }
}
