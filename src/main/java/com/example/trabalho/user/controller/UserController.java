package com.example.trabalho.user.controller;

import com.example.trabalho.user.dto.*;
import com.example.trabalho.user.model.ActivityHistory;
import com.example.trabalho.user.model.User;
import com.example.trabalho.user.model.UserSettings;
import com.example.trabalho.user.service.UserService;
import com.example.trabalho.user.service.UserSettingsService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserSettingsService userSettingsService;

    // Consultar perfil do usuário
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserProfile(
            @PathVariable Long userId
    ) {
        User user = userService.getUserById(userId);

        return ResponseEntity.ok(toUserDTO(user));
    }

    // Atualizar perfil do usuário
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileUpdateDTO dto
    ) {
        User user = userService.updateProfile(
                userId,
                dto.getName(),
                dto.getBiography(),
                dto.getProfileImage()
        );

        return ResponseEntity.ok(toUserDTO(user));
    }

    // Consultar histórico de atividades
    @GetMapping("/{userId}/activities")
    public ResponseEntity<List<ActivityHistoryResponseDTO>> getActivityHistory(
            @PathVariable Long userId
    ) {
        List<ActivityHistoryResponseDTO> activities =
                userService.getActivityHistory(userId)
                        .stream()
                        .map(this::toActivityDTO)
                        .toList();

        return ResponseEntity.ok(activities);
    }

    // Consultar configurações
    @GetMapping("/{userId}/settings")
    public ResponseEntity<UserSettingsResponseDTO> getSettings(
            @PathVariable Long userId
    ) {
        UserSettings settings = userSettingsService.getSettings(userId);

        return ResponseEntity.ok(toSettingsDTO(settings));
    }

    // Atualizar configurações
    @PatchMapping("/{userId}/settings")
    public ResponseEntity<UserSettingsResponseDTO> updateSettings(
            @PathVariable Long userId,
            @RequestBody UserSettingsUpdateDTO dto
    ) {
        UserSettings settings = userSettingsService.updateSettings(
                userId,
                dto.getNotificationPreferences()
        );

        return ResponseEntity.ok(toSettingsDTO(settings));
    }

    // Converter User para DTO
    private UserResponseDTO toUserDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .biography(user.getBiography())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .active(user.getActive())
                .build();
    }

    // Converter ActivityHistory para DTO
    private ActivityHistoryResponseDTO toActivityDTO(
            ActivityHistory activity
    ) {
        return ActivityHistoryResponseDTO.builder()
                .id(activity.getId())
                .activityType(activity.getActivityType())
                .description(activity.getDescription())
                .createdAt(activity.getCreatedAt())
                .build();
    }

    // Converter UserSettings para DTO
    private UserSettingsResponseDTO toSettingsDTO(
            UserSettings settings
    ) {
        return UserSettingsResponseDTO.builder()
                .id(settings.getId())
                .notificationPreferences(
                        settings.getNotificationPreferences()
                )
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}