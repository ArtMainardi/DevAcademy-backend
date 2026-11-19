package com.example.trabalho.SecurityModule;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDTO {

    private String message;
    private String token;
    private String tokenType;
}
