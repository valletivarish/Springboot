package com.monocept.myapp.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "instructor")
public class Instructor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private String email;

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH }, orphanRemoval = true, mappedBy = "instructor")
	@JsonManagedReference
	private List<Course> courses;

	public Instructor() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public void removeCourse(Course course) {
		for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).equals(course)) {
                courses.get(i).setInstructor(null);
            }
        }
//		if(courses!=null && courses.contains(course)) {
//			courses.remove(course);
//			course.setInstructor(null);
//		}
	}

	public void addCourse(Course course) {
		courses.add(course);
	}

	@Override
	public String toString() {
		return "Instructor [id=" + id + ", name=" + name + ", email=" + email + ", courses=" + courses + "]";
	}


	

}
