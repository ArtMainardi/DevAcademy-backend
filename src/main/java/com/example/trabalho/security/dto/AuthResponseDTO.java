package com.example.trabalho.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDTO {

    private String message;

    private String token;

    private String tokenType;

    private Long expiresIn;
}