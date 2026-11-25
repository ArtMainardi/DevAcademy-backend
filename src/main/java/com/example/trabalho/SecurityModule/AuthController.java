
package com.example.trabalho.SecurityModule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Cadastro de usuário
    @PostMapping("/cadastro")
    public ResponseEntity<MessageResponseDTO> cadastrar(
            @Valid @RequestBody CadastroRequestDTO dto) {

        authService.cadastrar(
                dto.getNome(),
                dto.getEmail(),
                dto.getSenha()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponseDTO(
                        "Usuário cadastrado com sucesso!"
                ));
    }

    // Login de usuário
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        String token = authService.autenticar(
                dto.getEmail(),
                dto.getSenha()
        );

        return ResponseEntity.ok(
                new AuthResponseDTO(
                        "Autenticação realizada com sucesso!",
                        token,
                        "Bearer"
                )
        );
    }

    // Logout de usuário
    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDTO> logout() {

        return ResponseEntity.ok(
                new MessageResponseDTO(
                        "Logout solicitado. Remova o token armazenado no cliente."
                )
        );
    }
}