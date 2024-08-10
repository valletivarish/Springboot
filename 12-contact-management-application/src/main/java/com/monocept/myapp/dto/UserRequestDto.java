package com.monocept.myapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDto {
	private long userId;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private boolean admin;
    private boolean active=true;
    @Email
    private String email;
    private String password="User@123";

}
