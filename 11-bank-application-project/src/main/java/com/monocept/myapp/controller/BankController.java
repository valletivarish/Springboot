package com.monocept.myapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.service.BankApplicationService;

@RestController
@RequestMapping("api/bank")
public class BankController {
	@SuppressWarnings("unused")
	private BankApplicationService bankApplicationService;

	public BankController(BankApplicationService bankApplicationService) {
		this.bankApplicationService = bankApplicationService;
	}
}
