package com.monocept.myapp.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.AccountResponseDto;
import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.ProfileRequestDto;
import com.monocept.myapp.dto.TransactionResponseDto;
import com.monocept.myapp.dto.UserResponseDto;
import com.monocept.myapp.service.BankApplicationService;
import com.monocept.myapp.util.PagedResponse;

@RestController
@RequestMapping("/api/bank-application")
@EnableMethodSecurity
public class BankApplicationController {

	private BankApplicationService bankApplicationService;

	public BankApplicationController(BankApplicationService bankApplicationService) {
		this.bankApplicationService = bankApplicationService;
	}

	@GetMapping("/admin/transactions")
	@PreAuthorize("hasRole('ADMIN')")
	public PagedResponse<TransactionResponseDto> getAllTransactions(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction,
			@RequestParam(name = "from", defaultValue = "#{T(java.time.LocalDateTime).now().minusDays(30).toString()}") String from,
			@RequestParam(name = "to", defaultValue = "#{T(java.time.LocalDateTime).now().toString()}") String to) {

		LocalDateTime fromDate = LocalDateTime.parse(from);
		LocalDateTime toDate = LocalDateTime.parse(to);

		return bankApplicationService.getAllTransactions(fromDate, toDate, page, size, sortBy, direction);
	}

	@PostMapping("/admin/customers/{userID}")
	@PreAuthorize("hasRole('ADMIN')")
	public UserResponseDto createCustomer(@RequestBody CustomerRequestDto customerRequestDto,
			@PathVariable(name = "userID") long userID) {
		return bankApplicationService.createCustomer(customerRequestDto, userID);
	}

	@PostMapping("/admin/banks/{bankId}/customers/{customerId}/accounts")
	@PreAuthorize("hasRole('ADMIN')")
	public CustomerResponseDto createAccount(@PathVariable(name = "customerId") long customerId,
			@PathVariable(name = "bankId") int bankId) {
		return bankApplicationService.createAccount(customerId, bankId);
	}

	@GetMapping("/admin/customers")
	@PreAuthorize("hasRole('ADMIN')")
	public List<CustomerResponseDto> getAllCustomers() {
		return bankApplicationService.getAllCustomers();
	}

	@GetMapping("/admin/customers/{customerId}")
	@PreAuthorize("hasRole('ADMIN')")
	public CustomerResponseDto getCustomerById(@PathVariable(name = "customerId") long customerId) {
		return bankApplicationService.getCustomerById(customerId);
	}

	@PostMapping("/customers/transactions")
	@PreAuthorize("hasRole('USER')")
	public TransactionResponseDto performTransaction(
			@RequestParam(name = "senderAccountNumber") long senderAccountNumber,
			@RequestParam(name = "receiverAccountNumber") long receiverAccountNumber,
			@RequestParam(name = "amount") double amount) {
		return bankApplicationService.performTransaction(senderAccountNumber, receiverAccountNumber, amount);
	}

	@GetMapping("/customers/passbook/{accountNumber}")
	@PreAuthorize("hasRole('USER')")
	public PagedResponse<TransactionResponseDto> getPassbook(@PathVariable(name = "accountNumber") long accountNumber,
			@RequestParam(name = "from", defaultValue = "#{T(java.time.LocalDateTime).now().minusDays(30).toString()}") String from,
			@RequestParam(name = "to", defaultValue = "#{T(java.time.LocalDateTime).now().toString()}") String to,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		LocalDateTime fromDate = LocalDateTime.parse(from);
		LocalDateTime toDate = LocalDateTime.parse(to);
		return bankApplicationService.getPassbook(accountNumber, fromDate, toDate, page, size, sortBy, direction);
	}

	@PutMapping("/customers/profile")
	@PreAuthorize("hasRole('USER')")
	public String updateProfile(@RequestBody ProfileRequestDto profileRequestDto) {
		return bankApplicationService.updateProfile(profileRequestDto);
	}

	@PutMapping("/customers/transactions/{accountNumber}/deposit")
	@PreAuthorize("hasRole('USER')")
	public AccountResponseDto deposit(@PathVariable(name = "accountNumber") long accountNumber,
			@RequestParam(name = "amount") double amount) {
		return bankApplicationService.depositAmount(accountNumber, amount);
	}
	@GetMapping("/customers/accounts")
	@PreAuthorize("hasRole('USER')")
	public List<AccountResponseDto> getAllAccounts() {
		return bankApplicationService.getAccounts();
	}

}
