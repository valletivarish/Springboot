package com.monocept.myapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.itextpdf.text.DocumentException;
import com.monocept.myapp.dto.AccountResponseDto;
import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.ProfileRequestDto;
import com.monocept.myapp.dto.TransactionResponseDto;
import com.monocept.myapp.dto.UserResponseDto;
import com.monocept.myapp.service.BankApplicationService;
import com.monocept.myapp.util.PagedResponse;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/bank")
@EnableMethodSecurity
public class BankApplicationController {

	private BankApplicationService bankApplicationService;

	public BankApplicationController(BankApplicationService bankApplicationService) {
		this.bankApplicationService = bankApplicationService;
	}

	@GetMapping("/admin/transactions")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PagedResponse<TransactionResponseDto>> getAllTransactions(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction,
			@RequestParam(name = "from", defaultValue = "#{T(java.time.LocalDateTime).now().minusDays(30).toString()}") String from,
			@RequestParam(name = "to", defaultValue = "#{T(java.time.LocalDateTime).now().toString()}") String to) {

		LocalDateTime fromDate = LocalDateTime.parse(from);
		LocalDateTime toDate = LocalDateTime.parse(to);

		return new ResponseEntity<PagedResponse<TransactionResponseDto>>(bankApplicationService.getAllTransactions(fromDate, toDate, page, size, sortBy, direction),HttpStatus.OK);
	}

	@PostMapping("/admin/customers/{userID}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponseDto> createCustomer(@RequestBody CustomerRequestDto customerRequestDto,
			@PathVariable(name = "userID") long userID) {
		return new ResponseEntity<UserResponseDto>(bankApplicationService.createCustomer(customerRequestDto, userID),HttpStatus.CREATED);
	}

	@PostMapping("/admin/banks/{bankId}/customers/{customerId}/accounts")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CustomerResponseDto> createAccount(@PathVariable(name = "customerId") long customerId,
			@PathVariable(name = "bankId") int bankId) {
		return new ResponseEntity<CustomerResponseDto>(bankApplicationService.createAccount(customerId, bankId),HttpStatus.CREATED);
	}

	@GetMapping("/admin/customers")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
		return new ResponseEntity<List<CustomerResponseDto>>(bankApplicationService.getAllCustomers(),HttpStatus.OK);
	}

	@GetMapping("/admin/customers/{customerId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable(name = "customerId") long customerId) {
		return new ResponseEntity<CustomerResponseDto>(bankApplicationService.getCustomerById(customerId),HttpStatus.OK);
	}

	@PostMapping("/customers/transactions")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<TransactionResponseDto> performTransaction(
			@RequestParam(name = "senderAccountNumber") long senderAccountNumber,
			@RequestParam(name = "receiverAccountNumber") long receiverAccountNumber,
			@RequestParam(name = "amount") double amount) {
		return new ResponseEntity<TransactionResponseDto>(bankApplicationService.performTransaction(senderAccountNumber, receiverAccountNumber, amount),HttpStatus.OK);
	}

	@GetMapping("/customers/passbook/{accountNumber}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<PagedResponse<TransactionResponseDto>> getPassbook(@PathVariable(name = "accountNumber") long accountNumber,
			@RequestParam(name = "from", defaultValue = "#{T(java.time.LocalDateTime).now().minusDays(30).toString()}") String from,
			@RequestParam(name = "to", defaultValue = "#{T(java.time.LocalDateTime).now().toString()}") String to,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) throws DocumentException, IOException, MessagingException {
		LocalDateTime fromDate = LocalDateTime.parse(from);
		LocalDateTime toDate = LocalDateTime.parse(to);
		return new ResponseEntity<PagedResponse<TransactionResponseDto>>(bankApplicationService.getPassbook(accountNumber, fromDate, toDate, page, size, sortBy, direction),HttpStatus.OK);
	}

	@PutMapping("/customers/profile")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> updateProfile(@RequestBody ProfileRequestDto profileRequestDto) {
		return new ResponseEntity<String>(bankApplicationService.updateProfile(profileRequestDto),HttpStatus.OK);
	}

	@PutMapping("/customers/{accountNumber}/deposit")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<AccountResponseDto> deposit(@PathVariable(name = "accountNumber") long accountNumber,
			@RequestParam(name = "amount") double amount) {
		return new ResponseEntity<AccountResponseDto>(bankApplicationService.depositAmount(accountNumber, amount),HttpStatus.OK);
	}
	@GetMapping("/customers/accounts")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
		return new ResponseEntity<List<AccountResponseDto>>(bankApplicationService.getAccounts(),HttpStatus.OK);
	}
	@DeleteMapping("admin/customers/{customerID}/deactivate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteCustomer(@PathVariable(name = "customerID")long customerID) {
		return new ResponseEntity<String>(bankApplicationService.deleteCustomer(customerID),HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("admin/customers/{customerID}/activate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> activateExistingCustomer(@PathVariable(name = "customerID")long customerID) {
		return new ResponseEntity<String>(bankApplicationService.activateCustomer(customerID),HttpStatus.OK);
	}
	@DeleteMapping("admin/customers/accounts/{accountNumber}/deactivate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteAccount(@PathVariable(name = "accountNumber")long accountNumber) {
		return new ResponseEntity<String>(bankApplicationService.deleteAccount(accountNumber),HttpStatus.NO_CONTENT);
	}
	@PutMapping("admin/customers/accounts/{accountNumber}/activate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> activateExistingAccount(@PathVariable(name = "accountNumber")long accountNumber) {
		return new ResponseEntity<String>(bankApplicationService.activateAccount(accountNumber),HttpStatus.OK);
	}
	
	@GetMapping("customers/accounts/{accountNumber}/view-balance")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<AccountResponseDto> viewBalance(@PathVariable(name = "accountNumber")long accountNumber) {
		return new ResponseEntity<AccountResponseDto>(bankApplicationService.viewBalance(accountNumber),HttpStatus.OK);
	}
	
	
	
	

}
