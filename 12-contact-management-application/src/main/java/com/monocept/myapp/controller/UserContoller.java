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

import com.monocept.myapp.dto.UserRequestDto;
import com.monocept.myapp.dto.UserResponseDto;
import com.monocept.myapp.entity.PagedResponse;
import com.monocept.myapp.service.ContactApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/users")
@Tag(name = "User Management", description = "Endpoints for managing user operations by admin")
public class UserContoller {
	@Autowired
	private ContactApplicationService contactApplicationService;

	@PostMapping
	@Operation(summary = "Create a new user.", description = "Creates a new user with the provided details.", tags = {
			"Users", "Create" })
	public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
		return new ResponseEntity<UserResponseDto>(contactApplicationService.createAndUpdateUser(userRequestDto),
				HttpStatus.CREATED);
	}

	@PutMapping
	@Operation(summary = "Update an existing user.", description = "Updates the details of an existing user.", tags = {
			"Users", "Update" })
	public ResponseEntity<UserResponseDto> updateUser(@RequestBody UserRequestDto userRequestDto) {

		return new ResponseEntity<UserResponseDto>(contactApplicationService.createAndUpdateUser(userRequestDto),
				HttpStatus.OK);
	}

	@GetMapping
	@Operation(summary = "Retrieve all users.", description = "Fetches a paginated and sorted list of all users.", tags = {
			"Users", "Get" })
	public ResponseEntity<PagedResponse<UserResponseDto>> getAllUsers(
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "sortBy", defaultValue = "userId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<PagedResponse<UserResponseDto>>(
				contactApplicationService.getAllUsers(page, size, sortBy, direction), HttpStatus.OK);
	}

	@GetMapping("{id}")
	@Operation(summary = "Retrieve a user by ID.", description = "Fetches detailed information for a user based on their ID.", tags = {
			"Users", "Get" })
	public ResponseEntity<UserResponseDto> getUserById(@PathVariable(name = "id") long id) {
		return new ResponseEntity<UserResponseDto>(contactApplicationService.getUserById(id), HttpStatus.OK);
	}

	@DeleteMapping("{id}")
	@Operation(summary = "Delete a user by ID.", description = "Deletes a user with the specified ID.", tags = {
			"Users", "Delete" })
	public ResponseEntity<String> deleteUser(@PathVariable(name = "id") long id) {
		return new ResponseEntity<String>(contactApplicationService.deleteUser(id), HttpStatus.OK);

	}
}
