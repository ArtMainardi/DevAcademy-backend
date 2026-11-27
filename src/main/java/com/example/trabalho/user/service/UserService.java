package com.example.trabalho.user.service;

import com.example.trabalho.user.model.ActivityHistory;
import com.example.trabalho.user.model.User;
import com.example.trabalho.user.model.UserSettings;
import com.example.trabalho.user.repository.ActivityHistoryRepository;
import com.example.trabalho.user.repository.UserRepository;
import com.example.trabalho.user.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final ActivityHistoryRepository activityHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    // Cadastrar usuário utilizando os dados individuais
    @Transactional
    public User createUser(
            String name,
            String email,
            String password
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Este email já está cadastrado."
            );
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .active(true)
                .build();

        user = userRepository.save(user);

        // Criar configurações padrão do usuário
        UserSettings settings = UserSettings.builder()
                .user(user)
                .notificationPreferences("{}")
                .build();

        userSettingsRepository.save(settings);

        // Registrar atividade inicial
        registerActivity(
                user,
                "ACCOUNT_CREATED",
                "Conta criada na plataforma."
        );

        return user;
    }

    // Cadastrar usuário utilizando uma entidade User
    @Transactional
    public User cadastrar(User user) {

        return createUser(
                user.getName(),
                user.getEmail(),
                user.getPassword()
        );
    }

    // Buscar usuário por ID
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );
    }

    // Buscar usuário por email
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );
    }

    // Atualizar perfil
    @Transactional
    public User updateProfile(
            Long userId,
            String name,
            String biography,
            String profileImage
    ) {
        User user = getUserById(userId);

        user.updateProfile(name, biography, profileImage);

        registerActivity(
                user,
                "PROFILE_UPDATED",
                "Perfil atualizado."
        );

        return userRepository.save(user);
    }

    // Alterar senha
    @Transactional
    public User changePassword(
            Long userId,
            String newPassword
    ) {
        User user = getUserById(userId);

        String hashedPassword = passwordEncoder.encode(newPassword);

        user.changePassword(hashedPassword);

        registerActivity(
                user,
                "PASSWORD_CHANGED",
                "Senha alterada."
        );

        return userRepository.save(user);
    }

    // Desativar conta
    @Transactional
    public void deactivateAccount(Long userId) {
        User user = getUserById(userId);

        user.deactivateAccount();

        userRepository.save(user);

        registerActivity(
                user,
                "ACCOUNT_DEACTIVATED",
                "Conta desativada."
        );
    }

    // Consultar histórico de atividades
    @Transactional(readOnly = true)
    public List<ActivityHistory> getActivityHistory(Long userId) {
        getUserById(userId);

        return activityHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Registrar atividade do usuário
    private void registerActivity(
            User user,
            String activityType,
            String description
    ) {
        ActivityHistory activity = ActivityHistory.builder()
                .user(user)
                .activityType(activityType)
                .description(description)
                .build();

        activityHistoryRepository.save(activity);
    }
}