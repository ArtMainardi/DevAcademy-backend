package com.example.trabalho.user.api;

import java.util.Optional;

public interface UserAuthenticationProvider {

    Optional<UserAuthenticationData> findByEmail(String email);
}