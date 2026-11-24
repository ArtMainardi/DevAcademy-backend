package com.example.trabalho.user.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class UserAuthenticationData {

    private String email;

    private String passwordHash;

    private boolean active;

    private Collection<String> authorities;
}