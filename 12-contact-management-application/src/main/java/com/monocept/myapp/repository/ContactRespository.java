package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Contact;

public interface ContactRespository extends JpaRepository<Contact, Integer> {

}
