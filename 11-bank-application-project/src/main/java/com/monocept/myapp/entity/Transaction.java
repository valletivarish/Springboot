package com.monocept.myapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_account")
	private Account senderAccount;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_account")
	private Account receiverAccount;
	
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	
	private LocalDateTime transactionDate=LocalDateTime.now();
	
	@NotNull
	private double amount;
	
}
