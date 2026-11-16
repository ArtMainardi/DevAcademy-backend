package com.example.trabalho.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    // Consultar todas as notificações do usuário
    @GetMapping("/{userId}")
    public ResponseEntity<Page<NotificationResponseDTO>> getUserNotifications(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        Page<NotificationResponseDTO> notifications =
                notificationService.getUserNotifications(userId, pageable)
                        .map(notificationMapper::toDTO);

        return ResponseEntity.ok(notifications);
    }

    // Consultar notificações não lidas
    @GetMapping("/{userId}/unread")
    public ResponseEntity<Page<NotificationResponseDTO>> getUnreadNotifications(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        Page<NotificationResponseDTO> notifications =
                notificationService.getUnreadNotifications(userId, pageable)
                        .map(notificationMapper::toDTO);

        return ResponseEntity.ok(notifications);
    }

    // Consultar quantidade de notificações não lidas
    @GetMapping("/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(
            @PathVariable Long userId
    ) {
        long count = notificationService.countUnreadNotifications(userId);

        return ResponseEntity.ok(count);
    }

    // Marcar notificação como lida
    @PatchMapping("/{userId}/{notificationId}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(
            @PathVariable Long userId,
            @PathVariable Long notificationId
    ) {
        Notification notification = notificationService.markAsRead(
                notificationId, userId
        );

        return ResponseEntity.ok(notificationMapper.toDTO(notification));
    }

    // Marcar notificação como não lida
    @PatchMapping("/{userId}/{notificationId}/unread")
    public ResponseEntity<NotificationResponseDTO> markAsUnread(
            @PathVariable Long userId,
            @PathVariable Long notificationId
    ) {
        Notification notification = notificationService.markAsUnread(
                notificationId, userId
        );

        return ResponseEntity.ok(notificationMapper.toDTO(notification));
    }
}