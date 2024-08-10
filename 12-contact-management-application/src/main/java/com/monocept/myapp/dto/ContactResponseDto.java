package com.monocept.myapp.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.monocept.myapp.entity.ContactDetail;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class ContactResponseDto {
private long contactId;
	
	private String firstName;
	
	private String lastName;
	
	private boolean active;
	
	private List<ContactDetail> contactDetails;
	
	private UserResponseDto user;
}
