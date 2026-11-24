package com.example.trabalho.user.api;

import com.example.trabalho.user.model.User;
import com.example.trabalho.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAuthenticationProviderImpl
        implements UserAuthenticationProvider {

    private final UserRepository userRepository;

    @Override
    public Optional<UserAuthenticationData> findByEmail(String email) {

        return userRepository.findByEmail(email)
                .map(user -> new UserAuthenticationData(
                        user.getEmail(),
                        user.getPassword(),
                        Boolean.TRUE.equals(user.getActive()),
                        List.of("ROLE_USER")
                ));
    }
}