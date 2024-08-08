package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monocept.myapp.entity.Instructor;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer>{
	


}
