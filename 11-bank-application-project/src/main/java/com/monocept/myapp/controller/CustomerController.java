package com.monocept.myapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.monocept.myapp.dto.AccountResponseDto;
import com.monocept.myapp.dto.ProfileRequestDto;
import com.monocept.myapp.dto.TransactionResponseDto;
import com.monocept.myapp.service.BankApplicationService;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/bank/customers")
@PreAuthorize("hasRole('USER')")
@Tag(name = "Customer Operations", description = "Endpoints for customer operations in the bank application.")
public class CustomerController {
	private BankApplicationService bankApplicationService;

	public CustomerController(BankApplicationService bankApplicationService) {
		this.bankApplicationService = bankApplicationService;
	}
	@PostMapping("/transactions")
	@Operation(
	        summary = "Perform a transaction between two accounts.",
	        description = "Initiates a transaction from the sender's account to the receiver's account with the specified amount.",
	        tags = { "Transactions", "Perform" }
	    )
	public ResponseEntity<TransactionResponseDto> performTransaction(
			@RequestParam(name = "senderAccountNumber") long senderAccountNumber,
			@RequestParam(name = "receiverAccountNumber") long receiverAccountNumber,
			@RequestParam(name = "amount") double amount) {
		return new ResponseEntity<TransactionResponseDto>(bankApplicationService.performTransaction(senderAccountNumber, receiverAccountNumber, amount),HttpStatus.OK);
	}

	@GetMapping("/passbook/{accountNumber}")
	@Operation(
	        summary = "Get the passbook for an account.",
	        description = "Retrieves the transaction history for the specified account within the given date range.",
	        tags = { "Passbook", "Get" }
	    )
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

	@PutMapping("/profile")
	@Operation(
	        summary = "Update the profile of the current user.",
	        description = "Allows the user to update their profile information.",
	        tags = { "Profile", "Update" }
	    )
	public ResponseEntity<String> updateProfile(@Valid @RequestBody ProfileRequestDto profileRequestDto) {
		return new ResponseEntity<String>(bankApplicationService.updateProfile(profileRequestDto),HttpStatus.OK);
	}

	@PutMapping("/{accountNumber}/deposit")
	@Operation(
	        summary = "Deposit amount into an account.",
	        description = "Deposits the specified amount into the given account.",
	        tags = { "Accounts", "Deposit" }
	    )
	public ResponseEntity<AccountResponseDto> deposit(@PathVariable(name = "accountNumber") long accountNumber,
			@RequestParam(name = "amount") double amount) {
		return new ResponseEntity<AccountResponseDto>(bankApplicationService.depositAmount(accountNumber, amount),HttpStatus.OK);
	}
	@GetMapping("/accounts")
	 @Operation(
		        summary = "Retrieve all accounts.",
		        description = "Fetches a list of all accounts for the user.",
		        tags = { "Accounts", "Get" }
		    )
	public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
		return new ResponseEntity<List<AccountResponseDto>>(bankApplicationService.getAccounts(),HttpStatus.OK);
	}
	@GetMapping("/accounts/{accountNumber}/view-balance")
	@Operation(
	        summary = "View balance of a specific account.",
	        description = "Retrieves the balance for the specified account number.",
	        tags = { "Accounts", "Get" }
	    )
	public ResponseEntity<AccountResponseDto> viewBalance(@PathVariable(name = "accountNumber")long accountNumber) {
		return new ResponseEntity<AccountResponseDto>(bankApplicationService.viewBalance(accountNumber),HttpStatus.OK);
	}
}
