package com.monocept.myapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterDto {
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	@NotNull
	private boolean admin;
	@NotBlank
	@Email
	private String email;
	@NotBlank
	private String password;

}
