package com.monocept.myapp.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.monocept.myapp.entity.Contact;
import com.monocept.myapp.entity.User;

public interface ContactRepository extends JpaRepository<Contact, Long>{

//	@Query("select c from Contact c where c.user=?1")
	Page<Contact> findByUser(User user, PageRequest pageRequest);

//	Optional<Contact> findByIdAndUser(long id, User user);

}
