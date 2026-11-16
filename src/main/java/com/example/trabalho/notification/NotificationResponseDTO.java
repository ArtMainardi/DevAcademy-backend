package com.example.trabalho.notification;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDTO {

    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    private boolean read;

    private String referenceType;
    private Long referenceId;
}