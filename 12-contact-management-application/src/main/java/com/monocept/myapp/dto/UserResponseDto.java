package com.monocept.myapp.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
@Data
@JsonInclude(value = Include.NON_NULL)
public class UserResponseDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private boolean admin;
    private boolean active;
    private String email;
    private String password;
    private List<ContactResponseDto> contacts;
}
