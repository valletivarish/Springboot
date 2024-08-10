package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.ContactDetail;

public interface ContactDetailRepository extends JpaRepository<ContactDetail, Integer> {

}
