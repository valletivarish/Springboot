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

import com.monocept.myapp.dto.ContactDetailRequestDto;
import com.monocept.myapp.dto.ContactDetailResponseDto;
import com.monocept.myapp.dto.ContactRequestDto;
import com.monocept.myapp.dto.ContactResponseDto;
import com.monocept.myapp.entity.PagedResponse;
import com.monocept.myapp.service.ContactApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/")
@Tag(name = "Contact Management", description = "Endpoints for managing contacts and their details.")
public class UserController {
	@Autowired
	private ContactApplicationService contactApplicationService;

	@PostMapping("contacts")
	@Operation(summary = "Create a new contact.", description = "Creates a new contact with the provided details.", tags = {
			"Contacts", "Create" })
	public ResponseEntity<ContactResponseDto> createContact(@Valid @RequestBody ContactRequestDto contactRequestDto) {
		return new ResponseEntity<ContactResponseDto>(
				contactApplicationService.createAndUpdateContact(contactRequestDto), HttpStatus.CREATED);

	}

	@GetMapping("contacts")
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

	@GetMapping("contacts/{id}")
	@Operation(summary = "Retrieve a contact by ID.", description = "Fetches detailed information for a contact based on their ID.", tags = {
			"Contacts", "Get" })
	public ResponseEntity<ContactResponseDto> getContactById(@PathVariable(name = "id") long id) {
		return new ResponseEntity<ContactResponseDto>(contactApplicationService.getContactById(id), HttpStatus.OK);
	}

	@PutMapping("contacts")
	@Operation(summary = "Update an existing contact.", description = "Updates the details of an existing contact.", tags = {
			"Contacts", "Update" })
	public ResponseEntity<ContactResponseDto> updateContact(@RequestBody ContactRequestDto contactRequestDto) {
		return new ResponseEntity<ContactResponseDto>(
				contactApplicationService.createAndUpdateContact(contactRequestDto), HttpStatus.OK);
	}

	@DeleteMapping("contacts/{id}")
	@Operation(summary = "Delete a contact by ID.", description = "Deletes a contact with the specified ID.", tags = {
			"Contacts", "Delete" })
	public ResponseEntity<String> deleteContact(@PathVariable(name = "id") long id) {
		return new ResponseEntity<String>(contactApplicationService.deleteContact(id), HttpStatus.OK);
	}

	@PostMapping("contacts/{contactId}/details")
	@Operation(summary = "Create contact detail for a specific contact.", description = "Creates a new contact detail associated with the specified contact ID.", tags = {
			"Contact Details", "Create" })
	public ResponseEntity<String> createContactDetail(@PathVariable(name = "contactId") long id,
			@RequestBody ContactDetailRequestDto contactDetailRequestDto) {
		return new ResponseEntity<String>(
				contactApplicationService.createAndUpdateContactDetail(id, contactDetailRequestDto),
				HttpStatus.CREATED);
	}

	@GetMapping("contacts/{contactId}/details")
	@Operation(summary = "Retrieve all contact details for a specific contact.", description = "Fetches a paginated and sorted list of all contact details associated with the specified contact ID.", tags = {
			"Contact Details", "Get" })
	public PagedResponse<ContactDetailResponseDto> getContactDetails(@PathVariable(name = "contactId") long id,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "sortBy", defaultValue = "contactDetailsId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return contactApplicationService.getAllContactDetails(id, page, size, sortBy, direction);
	}

	@PutMapping("contacts/{contactId}/details")
	@Operation(summary = "Update contact detail for a specific contact.", description = "Updates an existing contact detail associated with the specified contact ID.", tags = {
			"Contact Details", "Update" })
	public String updateContactDetail(@PathVariable(name = "contactId") long id,
			@RequestBody ContactDetailRequestDto contactDetailRequestDto) {
		return contactApplicationService.createAndUpdateContactDetail(id, contactDetailRequestDto);
	}

	@GetMapping("contacts/{contactId}/details/{contactDetailId}")
	@Operation(summary = "Retrieve a specific contact detail by ID.", description = "Fetches detailed information for a specific contact detail based on its ID.", tags = {
			"Contact Details", "Get" })
	public ContactDetailResponseDto getContactDetail(@PathVariable long contactId, @PathVariable long contactDetailId) {
		return contactApplicationService.getContactDetailById(contactId, contactDetailId);
	}

	@DeleteMapping("contacts/{contactId}/details/{contactDetailId}")
	@Operation(summary = "Delete a specific contact detail by ID.", description = "Deletes a contact detail based on its ID.", tags = {
			"Contact Details", "Delete" })
	public String deleteContactDetail(@PathVariable long contactId, @PathVariable long contactDetailId) {
		return contactApplicationService.deleteContactDetail(contactId, contactDetailId);
	}
}
