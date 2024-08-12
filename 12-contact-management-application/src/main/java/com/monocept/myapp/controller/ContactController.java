package com.monocept.myapp.controller;

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
import com.monocept.myapp.entity.PagedResponse;
import com.monocept.myapp.service.ContactApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/contacts")
@Tag(name = "Contact Management", description = "Endpoints for managing contacts by staff")
public class ContactController {
	@Autowired
	private ContactApplicationService contactApplicationService;

	@PostMapping
	@Operation(summary = "Create a new contact.", description = "Creates a new contact with the provided details.", tags = {
			"Contacts", "Create" })
	public ResponseEntity<ContactResponseDto> createContact(@Valid @RequestBody ContactRequestDto contactRequestDto) {
		return new ResponseEntity<ContactResponseDto>(
				contactApplicationService.createAndUpdateContact(contactRequestDto), HttpStatus.CREATED);

	}

	@GetMapping
	@Operation(summary = "Retrieve all contacts.", description = "Fetches a paginated and sorted list of all contacts.", tags = {
			"Contacts", "Get" })
	public ResponseEntity<PagedResponse<ContactResponseDto>> getAllContacts(
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "sortBy", defaultValue = "contactId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<PagedResponse<ContactResponseDto>>(
				contactApplicationService.getAllContacts(page, size, sortBy, direction), HttpStatus.OK);
	}

	@GetMapping("{id}")
	@Operation(summary = "Retrieve a contact by ID.", description = "Fetches detailed information for a contact based on their ID.", tags = {
			"Contacts", "Get" })
	public ResponseEntity<ContactResponseDto> getContactById(@PathVariable(name = "id") long id) {
		return new ResponseEntity<ContactResponseDto>(contactApplicationService.getContactById(id), HttpStatus.OK);
	}

	@PutMapping
	@Operation(summary = "Update an existing contact.", description = "Updates the details of an existing contact.", tags = {
			"Contacts", "Update" })
	public ResponseEntity<ContactResponseDto> updateContact(@RequestBody ContactRequestDto contactRequestDto) {
		return new ResponseEntity<ContactResponseDto>(
				contactApplicationService.createAndUpdateContact(contactRequestDto), HttpStatus.OK);
	}

	@DeleteMapping("{id}")
	@Operation(summary = "Delete a contact by ID.", description = "Deletes a contact with the specified ID.", tags = {
			"Contacts", "Delete" })
	public ResponseEntity<String> deleteContact(@PathVariable(name = "id") long id) {
		return new ResponseEntity<String>(contactApplicationService.deleteContact(id), HttpStatus.OK);
	}

	
}
