package com.example.trabalho.SecurityModule;

import com.seuprojeto.auth.dto.LoginRequestDTO;
import com.seuprojeto.auth.dto.CadastroRequestDTO;
import com.seuprojeto.auth.dto.AuthResponseDTO;
import com.seuprojeto.auth.dto.MessageResponseDTO;
import com.seuprojeto.user.User;

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

        User user = new User();

        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setSenha(dto.getSenha());

        authService.cadastrar(user);

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

        authService.logout();

        return ResponseEntity.ok(
                new MessageResponseDTO(
                        "Logout realizado com sucesso!"
                )
        );
    }
}
