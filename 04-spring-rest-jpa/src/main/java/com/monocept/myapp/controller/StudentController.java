package com.monocept.myapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.entity.Student;
import com.monocept.myapp.exception.StudentNotFoundException;
import com.monocept.myapp.service.StudentService;


@RestController
public class StudentController {
	
	private StudentService studentService;
	
	
	public StudentController(StudentService studentService) {
		super();
		this.studentService = studentService;
	}


	@GetMapping("students")
	public ResponseEntity<List<Student>> getAllStudents() {
		List<Student> students=studentService.getAllStudents();
		return new ResponseEntity<List<Student>>(students,HttpStatus.OK);
	}
	@GetMapping("students/{sid}")
	public ResponseEntity<Student> getStudentById(@PathVariable(name = "sid")int id) {
		Student student=studentService.getStudentById(id);
		if(student==null) {
			throw new StudentNotFoundException("student with id "+id+" not found");
		}
		
		return new ResponseEntity<Student>(student,HttpStatus.OK);
	}
	@PostMapping("students")
	public ResponseEntity<Student> addStudent(@RequestBody Student student) {
		Student student1 = studentService.saveAndUpdate(student);
		return new ResponseEntity<Student>(student1,HttpStatus.CREATED);
	}
	
	@PutMapping("students")
	public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
		if(studentService.getStudentById(student.getId())==null) {
			throw new StudentNotFoundException("student with id "+student.getId()+" not found");
		}
		Student student1=studentService.saveAndUpdate(student);
		return new ResponseEntity<Student>(student1,HttpStatus.OK);
	}
	@DeleteMapping("students/{sid}")
	public ResponseEntity<HttpStatus> deleteStudent(@PathVariable(name = "sid") int id) {
		Student tempStudent=studentService.getStudentById(id);
		if(tempStudent==null) {
			throw new StudentNotFoundException("student with id "+id+" not found");
		}
		studentService.deleteStudent(id);
		return new ResponseEntity<HttpStatus>(HttpStatus.OK);
	}
//	@ExceptionHandler
//	public ResponseEntity<ResponseErrorNotFound> execeptionHandler(StudentNotFoundException ex){
//		ResponseErrorNotFound error=new ResponseErrorNotFound();
//		error.setStatus(HttpStatus.NOT_FOUND.value());
//		error.setMessage(ex.getMessage());
//		error.setDate(LocalDateTime.now());
//		
//		
//		return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
//		
//	}
	
	
	
}
