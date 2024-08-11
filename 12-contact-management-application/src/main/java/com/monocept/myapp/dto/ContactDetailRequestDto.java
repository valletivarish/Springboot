package com.monocept.myapp.dto;

import com.monocept.myapp.entity.Contact;
import com.monocept.myapp.entity.ContactType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContactDetailRequestDto {
	private int contactDetailsId;
	@NotNull
	
	private ContactType contactType;
	private Contact contact;
}
