package com.example.trabalho.security.service;

import com.example.trabalho.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Realiza a autenticação do usuário e gera um token JWT.
     *
     * @param email email utilizado para autenticação
     * @param senha senha informada pelo usuário
     * @return token JWT gerado
     */
    public String autenticar(String email, String senha) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                senha
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        return jwtService.generateToken(userDetails);
    }

    /**
     * Retorna o tempo de validade do token JWT.
     *
     * @return tempo de validade em milissegundos
     */
    public long getExpiration() {
        return jwtService.getExpiration();
    }
}