package com.monocept.myapp.service;

import java.util.List;

import org.springframework.http.HttpStatusCode;

import com.monocept.myapp.dto.ContactDetailRequestDto;
import com.monocept.myapp.dto.ContactDetailResponseDto;
import com.monocept.myapp.dto.ContactRequestDto;
import com.monocept.myapp.dto.ContactResponseDto;
import com.monocept.myapp.dto.UserRequestDto;
import com.monocept.myapp.dto.UserResponseDto;
import com.monocept.myapp.entity.PagedResponse;

public interface ContactApplicationService {
	
	public PagedResponse<UserResponseDto> getAllUsers(int size, int page, String sortBy, String direction);

	public UserResponseDto createAndUpdateUser(UserRequestDto userRequestDto);

	public UserResponseDto getUserById(long id);

	public String deleteUser(long id);

	public ContactResponseDto createAndUpdateContact(ContactRequestDto contactRequestDto);

	public PagedResponse<ContactResponseDto> getAllContacts(int page, int size, String sortBy, String direction);

	public ContactResponseDto getContactById(long id);

	public String deleteContact(long id);

	public String createAndUpdateContactDetail(long id, ContactDetailRequestDto contactDetailRequestDto);


	public PagedResponse<ContactDetailResponseDto> getAllContactDetails(long id, int page, int size, String sortBy, String direction);

	public ContactDetailResponseDto getContactDetailById(long contactId, long contactDetailId);

	public String deleteContactDetail(long contactId, long contactDetailId);

}
