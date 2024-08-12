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
import com.monocept.myapp.entity.PagedResponse;
import com.monocept.myapp.service.ContactApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/contacts/{contactId}/details")
@Tag(name = "Contact Details Management", description = "Endpoints for managing contact details by staff")
public class ContactDetailController {
	@Autowired
	private ContactApplicationService contactApplicationService;
	
	@PostMapping
	@Operation(summary = "Create contact detail for a specific contact.", description = "Creates a new contact detail associated with the specified contact ID.", tags = {
			"Contact Details", "Create" })
	public ResponseEntity<String> createContactDetail(@PathVariable(name = "contactId") long id,
			@RequestBody ContactDetailRequestDto contactDetailRequestDto) {
		return new ResponseEntity<String>(
				contactApplicationService.createAndUpdateContactDetail(id, contactDetailRequestDto),
				HttpStatus.CREATED);
	}

	@GetMapping
	@Operation(summary = "Retrieve all contact details for a specific contact.", description = "Fetches a paginated and sorted list of all contact details associated with the specified contact ID.", tags = {
			"Contact Details", "Get" })
	public PagedResponse<ContactDetailResponseDto> getContactDetails(@PathVariable(name = "contactId") long id,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "sortBy", defaultValue = "contactDetailsId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return contactApplicationService.getAllContactDetails(id, page, size, sortBy, direction);
	}

	@PutMapping
	@Operation(summary = "Update contact detail for a specific contact.", description = "Updates an existing contact detail associated with the specified contact ID.", tags = {
			"Contact Details", "Update" })
	public String updateContactDetail(@PathVariable(name = "contactId") long id,
			@RequestBody ContactDetailRequestDto contactDetailRequestDto) {
		return contactApplicationService.createAndUpdateContactDetail(id, contactDetailRequestDto);
	}

	@GetMapping("{contactDetailId}")
	@Operation(summary = "Retrieve a specific contact detail by ID.", description = "Fetches detailed information for a specific contact detail based on its ID.", tags = {
			"Contact Details", "Get" })
	public ContactDetailResponseDto getContactDetail(@PathVariable long contactId, @PathVariable long contactDetailId) {
		return contactApplicationService.getContactDetailById(contactId, contactDetailId);
	}

	@DeleteMapping("{contactDetailId}")
	@Operation(summary = "Delete a specific contact detail by ID.", description = "Deletes a contact detail based on its ID.", tags = {
			"Contact Details", "Delete" })
	public String deleteContactDetail(@PathVariable long contactId, @PathVariable long contactDetailId) {
		return contactApplicationService.deleteContactDetail(contactId, contactDetailId);
	}
	
	
	
}
