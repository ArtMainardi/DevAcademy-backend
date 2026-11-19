package com.example.trabalho.user.dto;

import lombok.Data;

@Data
public class UserCreateDTO {

    private String name;
    private String email;
    private String password;
}