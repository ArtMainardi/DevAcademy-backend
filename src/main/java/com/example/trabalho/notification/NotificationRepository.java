package com.example.trabalho.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Listar notificações de um usuário, ordenadas por data
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    // Listar notificações não lidas de um usuário
    Page<Notification> findByRecipientIdAndReadAtIsNullOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    // Contar notificações não lidas de um usuário
    long countByRecipientIdAndReadAtIsNull(Long userId);

    // Buscar notificações de um usuário por tipo
    Page<Notification> findByRecipientIdAndTypeOrderByCreatedAtDesc(
            Long userId,
            NotificationType type,
            Pageable pageable
    );

    // Buscar notificações por tipo
    Page<Notification> findByTypeOrderByCreatedAtDesc(
            NotificationType type,
            Pageable pageable
    );
}