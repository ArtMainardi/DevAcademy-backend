package com.example.trabalho.user.service;

import com.example.trabalho.user.model.UserSettings;
import com.example.trabalho.user.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserService userService;

    // Consultar configurações do usuário
    @Transactional(readOnly = true)
    public UserSettings getSettings(Long userId) {

        userService.getUserById(userId);

        return userSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Configurações do usuário não encontradas."
                ));
    }

    // Atualizar configurações do usuário
    @Transactional
    public UserSettings updateSettings(
            Long userId,
            String notificationPreferences
    ) {
        UserSettings settings = getSettings(userId);

        if (notificationPreferences != null) {
            settings.setNotificationPreferences(notificationPreferences);
        }

        return userSettingsRepository.save(settings);
    }
}