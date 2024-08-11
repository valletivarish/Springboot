package com.monocept.myapp.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Contact;
import com.monocept.myapp.entity.ContactDetail;
import com.monocept.myapp.entity.User;

public interface ContactDetailRepository extends JpaRepository<ContactDetail, Integer> {

	Page<ContactDetail> findByContact(Contact contact, PageRequest pageRequest);

//	Optional<ContactDetail> findByContactDetailId(long id);


}
