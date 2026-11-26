package com.example.trabalho.security.controller;

import com.example.trabalho.security.dto.AuthResponseDTO;
import com.example.trabalho.security.dto.LoginRequestDTO;
import com.example.trabalho.security.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Realiza a autenticação do usuário.
     *
     * Recebe email e senha, solicita a autenticação
     * ao AuthService e retorna o token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        String token = authService.autenticar(
                request.getEmail(),
                request.getSenha()
        );

        AuthResponseDTO response = new AuthResponseDTO(
                "Autenticação realizada com sucesso!",
                token,
                "Bearer"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Trata falhas de autenticação.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(
            AuthenticationException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Email ou senha inválidos."
        );

        problem.setTitle("Falha na autenticação");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(problem);
    }
}