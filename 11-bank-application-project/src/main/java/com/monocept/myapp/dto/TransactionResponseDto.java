package com.monocept.myapp.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.monocept.myapp.entity.TransactionType;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class TransactionResponseDto {

	private long id;

	private AccountResponseDto senderAccount;
	
	private AccountResponseDto receiverAccount;

	private TransactionType transactionType;

	private LocalDateTime transactionDate;

	private double amount;
	


}
