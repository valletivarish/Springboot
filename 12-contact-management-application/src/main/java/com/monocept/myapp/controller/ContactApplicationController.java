package com.monocept.myapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.ContactRequestDto;
import com.monocept.myapp.dto.ContactResponseDto;
import com.monocept.myapp.dto.UserRequestDto;
import com.monocept.myapp.dto.UserResponseDto;
import com.monocept.myapp.entity.PagedResponse;
import com.monocept.myapp.service.ContactApplicationService;

@RestController
@RequestMapping("api/")
public class ContactApplicationController {
	@Autowired
	private ContactApplicationService contactApplicationService;
	
	@PostMapping("users")
	public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
		return new ResponseEntity<UserResponseDto>(contactApplicationService.createAndUpdateUser(userRequestDto),HttpStatus.CREATED);
	}
	@PutMapping("users")
	public ResponseEntity<UserResponseDto> updateUser(@RequestBody UserRequestDto userRequestDto) {
		
		return new ResponseEntity<UserResponseDto>(contactApplicationService.createAndUpdateUser(userRequestDto),HttpStatus.OK);
	}
	
	
	@GetMapping("users")
	public ResponseEntity<PagedResponse<UserResponseDto>> getAllUsers(@RequestParam(name = "size",defaultValue = "5") int size,@RequestParam(name = "page", defaultValue = "0")int page,@RequestParam(name = "sortBy",defaultValue = "userId")String sortBy,@RequestParam(name = "direction",defaultValue = "asc")String direction) {
		return new ResponseEntity<PagedResponse<UserResponseDto>>(contactApplicationService.getAllUsers(page,size,sortBy,direction),HttpStatus.OK);
	}
	
	@GetMapping("users/{id}")
	public ResponseEntity<UserResponseDto> getUserById(@PathVariable(name = "id")long id) {
		return new ResponseEntity<UserResponseDto>(contactApplicationService.getUserById(id),HttpStatus.OK);
	}
	
	@DeleteMapping("users/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable(name = "id")long id){
		return new ResponseEntity<String>(contactApplicationService.deleteUser(id),HttpStatus.OK);
		
	}
	
	@PostMapping("contacts")
	public ResponseEntity<ContactResponseDto> createContact(@RequestBody ContactRequestDto contactRequestDto){
		return new ResponseEntity<ContactResponseDto>(contactApplicationService.createAndUpdateContact(contactRequestDto),HttpStatus.CREATED);
		
	}
	@GetMapping("contacts")
	public ResponseEntity<PagedResponse<ContactResponseDto>> getAllContacts(@RequestParam(name = "size",defaultValue = "5") int size,@RequestParam(name = "page", defaultValue = "0")int page,@RequestParam(name = "sortBy",defaultValue = "contactId")String sortBy,@RequestParam(name = "direction",defaultValue = "asc")String direction) {
		return new ResponseEntity<PagedResponse<ContactResponseDto>>(contactApplicationService.getAllContacts(page,size,sortBy,direction),HttpStatus.OK);
	}
	
	
}
