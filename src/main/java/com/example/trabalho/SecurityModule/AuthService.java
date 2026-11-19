package com.example.trabalho.SecurityModule;

import com.seuprojeto.user.User;
import com.seuprojeto.user.UserRepository;
import com.seuprojeto.user.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Cadastro de usuário
    @Transactional
    public User cadastrar(User user) {

        // Criptografa a senha antes de salvar
        user.setSenha(passwordEncoder.encode(user.getSenha()));

        // Define o usuário como ativo
        user.setAtivo(true);

        return userService.salvar(user);
    }

    // Autenticação do usuário
    public String autenticar(String email, String senha) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                senha
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        return jwtService.gerarToken(authentication);
    }

    // Verifica as credenciais do usuário
    public boolean verificarCredenciais(String email, String senha) {

        return userRepository.findByEmail(email)
                .filter(user ->
                        passwordEncoder.matches(
                                senha,
                                user.getSenha()
                        )
                )
                .filter(User::isAtivo)
                .isPresent();
    }

    // Encerramento da autenticação
    public void logout() {

        SecurityContextHolder.clearContext();

        // Em JWT stateless, limpar o contexto local não invalida
        // um token que já foi emitido.
    }
}
