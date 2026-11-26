package com.example.trabalho.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserSettingsResponseDTO {

    private Long id;
    private String notificationPreferences;
    private LocalDateTime updatedAt;
}