package com.github.shubhampdev7.linkedInProject.userService.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
