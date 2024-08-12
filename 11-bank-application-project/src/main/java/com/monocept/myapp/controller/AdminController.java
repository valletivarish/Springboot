package com.monocept.myapp.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.TransactionResponseDto;
import com.monocept.myapp.dto.UserResponseDto;
import com.monocept.myapp.service.BankApplicationService;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/bank/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations", description = "Endpoints for managing bank operations as an admin.")
public class AdminController {
	private BankApplicationService bankApplicationService;

	public AdminController(BankApplicationService bankApplicationService) {
		this.bankApplicationService = bankApplicationService;
	}

	
	@GetMapping("/transactions")
	@Operation(summary = "Retrieve all transactions within a specified date range.", description = "Fetches all transactions for the bank between the specified dates. Pagination and sorting are supported.", tags = {
			"Transactions", "Get" })
	public ResponseEntity<PagedResponse<TransactionResponseDto>> getAllTransactions(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction,
			@RequestParam(name = "from", defaultValue = "#{T(java.time.LocalDateTime).now().minusDays(30).toString()}") String from,
			@RequestParam(name = "to", defaultValue = "#{T(java.time.LocalDateTime).now().toString()}") String to) {

		LocalDateTime fromDate = LocalDateTime.parse(from);
		LocalDateTime toDate = LocalDateTime.parse(to);

		return new ResponseEntity<PagedResponse<TransactionResponseDto>>(
				bankApplicationService.getAllTransactions(fromDate, toDate, page, size, sortBy, direction),
				HttpStatus.OK);
	}

	
	@PostMapping("/customers/{userID}")
	@Operation(summary = "Create a new customer associated with a specific user ID.", description = "Creates a new customer record associated with the provided user ID. Requires admin privileges.", tags = {
			"Customers", "Create" })
	public ResponseEntity<UserResponseDto> createCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto,
			@PathVariable(name = "userID") long userID) {
		return new ResponseEntity<UserResponseDto>(bankApplicationService.createCustomer(customerRequestDto, userID),
				HttpStatus.CREATED);
	}

	
	@PostMapping("/banks/{bankId}/customers/{customerId}/accounts")
	@Operation(summary = "Create a new account for a specific customer within a given bank.", description = "Creates a new account for the specified customer and bank. Requires admin privileges.", tags = {
			"Accounts", "Create" })
	public ResponseEntity<CustomerResponseDto> createAccount(@Valid @PathVariable(name = "customerId") long customerId,
			@PathVariable(name = "bankId") int bankId) {
		return new ResponseEntity<CustomerResponseDto>(bankApplicationService.createAccount(customerId, bankId),
				HttpStatus.CREATED);
	}


	@GetMapping("/customers")
	@Operation(summary = "Retrieve a list of all customers.", description = "Fetches a list of all customer records in the bank.", tags = {
			"Customers", "Get" })
	public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
		return new ResponseEntity<List<CustomerResponseDto>>(bankApplicationService.getAllCustomers(), HttpStatus.OK);
	}

	
	@GetMapping("/customers/{customerId}")
	@Operation(summary = "Retrieve details of a specific customer by their ID.", description = "Fetches detailed information for a customer based on their ID.", tags = {
			"Customers", "Get" })
	public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable(name = "customerId") long customerId) {
		return new ResponseEntity<CustomerResponseDto>(bankApplicationService.getCustomerById(customerId),
				HttpStatus.OK);
	}

	
	@DeleteMapping("/customers/{customerID}/deactivate")
	@Operation(summary = "Deactivate a customer account using their ID.", description = "Deactivates a customer account by its ID. This action requires admin privileges.", tags = {
			"Customers", "Delete" })
	public ResponseEntity<String> deleteCustomer(@PathVariable(name = "customerID") long customerID) {
		return new ResponseEntity<String>(bankApplicationService.deleteCustomer(customerID), HttpStatus.NO_CONTENT);
	}

	@PutMapping("/customers/{customerID}/activate")
	@Operation(summary = "Reactivate a customer account using their ID.", description = "Reactivates a customer account by its ID. This action requires admin privileges.", tags = {
			"Customers", "Update" })
	public ResponseEntity<String> activateExistingCustomer(@PathVariable(name = "customerID") long customerID) {
		return new ResponseEntity<String>(bankApplicationService.activateCustomer(customerID), HttpStatus.OK);
	}

	@DeleteMapping("/customers/accounts/{accountNumber}/deactivate")
	@Operation(summary = "Deactivate an account using its account number.", description = "Deactivates an account based on its account number. Requires admin privileges.", tags = {
			"Accounts", "Delete" })
	public ResponseEntity<String> deleteAccount(@PathVariable(name = "accountNumber") long accountNumber) {
		return new ResponseEntity<String>(bankApplicationService.deleteAccount(accountNumber), HttpStatus.NO_CONTENT);
	}

	@PutMapping("/customers/accounts/{accountNumber}/activate")
	@Operation(summary = "Reactivate an account using its account number.", description = "Reactivates an account based on its account number. Requires admin privileges.", tags = {
			"Accounts", "Update" })
	public ResponseEntity<String> activateExistingAccount(@PathVariable(name = "accountNumber") long accountNumber) {
		return new ResponseEntity<String>(bankApplicationService.activateAccount(accountNumber), HttpStatus.OK);
	}
}
