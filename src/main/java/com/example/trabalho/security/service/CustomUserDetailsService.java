package com.example.trabalho.security.service;

import com.example.trabalho.user.api.UserAuthenticationProvider;
import com.example.trabalho.user.api.UserAuthenticationData;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAuthenticationProvider userAuthenticationProvider;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        UserAuthenticationData userData =
                userAuthenticationProvider.findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Usuário não encontrado."
                                )
                        );

        return User.builder()
                .username(userData.getEmail())
                .password(userData.getPasswordHash())
                .authorities(userData.getAuthorities().toArray(new String[0]))
                .disabled(!userData.isActive())
                .build();
    }
}