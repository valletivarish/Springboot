package com.monocept.myapp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.CourseResponseDTO;
import com.monocept.myapp.dto.StudentResponseDTO;
import com.monocept.myapp.entity.Course;
import com.monocept.myapp.entity.Student;
import com.monocept.myapp.exceptions.ResourceNotFoundException;
import com.monocept.myapp.repository.CourseRepository;
import com.monocept.myapp.repository.StudentRepository;

@Service
public class StudentCourseServiceImpl implements StudentCourseService{
	private StudentRepository studentRepository;
	private CourseRepository courseRepository;
	
	
	public StudentCourseServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository) {
		super();
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
	}


	@Override
	public StudentResponseDTO createStudent(Student student) {
		return convertStudentToStudentResponseDTO(studentRepository.save(student));
	}


	@Override
	public List<StudentResponseDTO> getAllStudents() {
		List<Student> students = studentRepository.findAll();

		return convertStudentToStudentResponseDTO(students);

	}


	private List<StudentResponseDTO> convertStudentToStudentResponseDTO(List<Student> students) {
	    List<StudentResponseDTO> studentsDTO = new ArrayList<>();
	    for (Student s : students) {
	        StudentResponseDTO studentDTO = new StudentResponseDTO();
	        studentDTO.setId(s.getId());
	        studentDTO.setStudentName(s.getName());
	        
	        List<CourseResponseDTO> courses = new ArrayList<>();
	        for (Course c : s.getCourses()) {
	            CourseResponseDTO courseResponseDTO = new CourseResponseDTO();
	            courseResponseDTO.setId(c.getId());
	            courseResponseDTO.setCourseName(c.getCourseName());
	            courses.add(courseResponseDTO);
	        }
	        studentDTO.setCourses(courses);;
	        studentsDTO.add(studentDTO);
	        
	    }
	    return studentsDTO;
	}



	@Override
	public StudentResponseDTO getStudentByID(long id) {
		Student student = studentRepository.findById(id);
		if(student==null) {
			throw new ResourceNotFoundException("resources not found");
		}
		return convertStudentToStudentResponseDTO(student);
	}


	private StudentResponseDTO convertStudentToStudentResponseDTO(Student student) {
		StudentResponseDTO studentDTO = new StudentResponseDTO();
        studentDTO.setId(student.getId());
        studentDTO.setStudentName(student.getName());
        
        List<CourseResponseDTO> courses = new ArrayList<>();
        for (Course c : student.getCourses()) {
            CourseResponseDTO courseDTO = new CourseResponseDTO();
            courseDTO.setId(c.getId());
            courseDTO.setCourseName(c.getCourseName());
            courses.add(courseDTO);
        }
        studentDTO.setCourses(courses);
		return studentDTO;
	}


	@Override
	public String deleteStudentByID(long id) {
		Student student = studentRepository.findById(id);
		if(student==null) {
			throw new ResourceNotFoundException("Resource not found");
		}
		studentRepository.delete(student);
		return "Deleted Succesfully";
	}


	@Override
	public CourseResponseDTO createCourse(Course course) {
		return convertCourseToCourseDTO(courseRepository.save(course));
	}


	@Override
	public List<CourseResponseDTO> getAllCourses() {
		List<Course> courses = courseRepository.findAll();
		return convertCoursesToCourseResponseDTO(courses);
	}


	private List<CourseResponseDTO> convertCoursesToCourseResponseDTO(List<Course> courses) {
		List<CourseResponseDTO> coursesDTO=new ArrayList<CourseResponseDTO>();
		for(Course c:courses) {
			CourseResponseDTO courseResponseDTO=new CourseResponseDTO();
			courseResponseDTO.setId(c.getId());
			courseResponseDTO.setCourseName(c.getCourseName());
			List<StudentResponseDTO> students=new ArrayList<StudentResponseDTO>();
			for(Student s:c.getStudents()) {
				StudentResponseDTO studentResponseDTO=new StudentResponseDTO();
				studentResponseDTO.setId(s.getId());
				studentResponseDTO.setStudentName(s.getName());
				students.add(studentResponseDTO);
			}
			courseResponseDTO.setStudents(students);
			coursesDTO.add(courseResponseDTO);
		}
		return coursesDTO;
	}


	@Override
	public CourseResponseDTO getCourseByID(int id) {
		Course course = courseRepository.findById(id).orElse(null);
		if(course==null) {
			throw new ResourceNotFoundException("Resource not found");
		}
		return convertCourseToCourseDTO(course);
	}


	private CourseResponseDTO convertCourseToCourseDTO(Course course) {
		CourseResponseDTO courseResponseDTO=new CourseResponseDTO();
		courseResponseDTO.setId(course.getId());
		courseResponseDTO.setCourseName(course.getCourseName());
		List<StudentResponseDTO> students=new ArrayList<StudentResponseDTO>();
		for(Student s:course.getStudents()) {
			StudentResponseDTO studentResponseDTO=new StudentResponseDTO();
			studentResponseDTO.setId(s.getId());
			studentResponseDTO.setStudentName(s.getName());
			students.add(studentResponseDTO);
		}
		courseResponseDTO.setStudents(students);
		return courseResponseDTO;
	}


	@Override
	public String deleteCourseByID(int id) {
		Course course = courseRepository.findById(id).orElse(null);
		if(course==null) {
			throw new ResourceNotFoundException("Resource not found");
		}
		for (Student student : course.getStudents()) {
            student.getCourses().remove(course);
            studentRepository.save(student);
        }
        courseRepository.deleteById(id);
        return "Course deleted successfully";

	}


	@Override
	public CourseResponseDTO assignStudentToCourse(long studentID, int courseID) {
		Student student=studentRepository.findById(studentID);
		if(student==null) {
			throw new ResourceNotFoundException("Resource not found");
		}
		Course course=courseRepository.findById(courseID).orElse(null);
		if(course==null) {
			throw new ResourceNotFoundException("Resource not found");
		}
		course.getStudents().add(student);
		student.getCourses().add(course);
		courseRepository.save(course);
		studentRepository.save(student);
		return convertCourseToCourseDTO(course);
		
		
	}


	@Override
	public CourseResponseDTO deleteStudentFromCourse(long studentID, int courseID) {
		Student student=studentRepository.findById(studentID);
		if(student==null) {
			throw new ResourceNotFoundException("Resource not found");
		}
		Course course=courseRepository.findById(courseID).orElse(null);
		if(course==null) {
			throw new ResourceNotFoundException("Resource not found");
		}
		course.getStudents().remove(student);
		student.getCourses().remove(course);
		courseRepository.save(course);
		studentRepository.save(student);
		return convertCourseToCourseDTO(course);
	}
	
}
