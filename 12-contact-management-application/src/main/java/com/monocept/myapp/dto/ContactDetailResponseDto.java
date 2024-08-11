package com.monocept.myapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.monocept.myapp.entity.Contact;
import com.monocept.myapp.entity.ContactType;

import lombok.Data;

@Data
@JsonInclude(content = Include.NON_NULL)
public class ContactDetailResponseDto {
	private long contactDetailsId;
	private ContactType contactType;

	private ContactResponseDto contact;
}
