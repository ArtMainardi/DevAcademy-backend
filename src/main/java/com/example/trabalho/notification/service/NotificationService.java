package com.example.trabalho.notification.service;

import com.example.trabalho.notification.model.NotificationType;
import com.example.trabalho.notification.model.Notification;
import com.example.trabalho.notification.repository.NotificationRepository;
import com.example.trabalho.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Criar notificação
    @Transactional
    public Notification createNotification(
            User recipient,
            String title,
            String message,
            NotificationType type,
            String referenceType,
            Long referenceId
    ) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .createdAt(LocalDateTime.now())
                .build();

        return notificationRepository.save(notification);
    }

    // Consultar notificações do usuário
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(
            Long userId,
            Pageable pageable
    ) {
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(userId, pageable);
    }

    // Consultar notificações não lidas
    @Transactional(readOnly = true)
    public Page<Notification> getUnreadNotifications(
            Long userId,
            Pageable pageable
    ) {
        return notificationRepository
                .findByRecipientIdAndReadAtIsNullOrderByCreatedAtDesc(
                        userId, pageable
                );
    }

    // Marcar notificação como lida
    @Transactional
    public Notification markAsRead(Long notificationId, Long userId) {
        Notification notification = getUserNotification(notificationId, userId);

        notification.markAsRead();

        return notificationRepository.save(notification);
    }

    // Marcar notificação como não lida
    @Transactional
    public Notification markAsUnread(Long notificationId, Long userId) {
        Notification notification = getUserNotification(notificationId, userId);

        notification.markAsUnread();

        return notificationRepository.save(notification);
    }

    // Contar notificações não lidas
    @Transactional(readOnly = true)
    public long countUnreadNotifications(Long userId) {
        return notificationRepository
                .countByRecipientIdAndReadAtIsNull(userId);
    }

    // Criar notificação para cada acontecimento do sistema

    public Notification notifyLike(
            User recipient, Long publicationId
    ) {
        return createNotification(
                recipient,
                "Nova curtida",
                "Sua publicação recebeu uma nova curtida.",
                NotificationType.LIKE,
                "PUBLICATION",
                publicationId
        );
    }

    public Notification notifyComment(
            User recipient, Long publicationId
    ) {
        return createNotification(
                recipient,
                "Novo comentário",
                "Sua publicação recebeu um novo comentário.",
                NotificationType.COMMENT,
                "PUBLICATION",
                publicationId
        );
    }

    public Notification notifyCorrectionRequest(
            User recipient, Long publicationId
    ) {
        return createNotification(
                recipient,
                "Nova solicitação de correção",
                "Uma nova solicitação de correção foi enviada para sua publicação.",
                NotificationType.CORRECTION_REQUEST,
                "PUBLICATION",
                publicationId
        );
    }

    public Notification notifyCorrectionUpdate(
            User recipient, Long correctionRequestId
    ) {
        return createNotification(
                recipient,
                "Atualização na solicitação de correção",
                "Uma solicitação de correção recebeu uma atualização.",
                NotificationType.CORRECTION_UPDATE,
                "CORRECTION_REQUEST",
                correctionRequestId
        );
    }

    public Notification notifyReputationUpdate(
            User recipient
    ) {
        return createNotification(
                recipient,
                "Reputação atualizada",
                "Sua reputação foi atualizada.",
                NotificationType.REPUTATION_UPDATE,
                "USER",
                recipient.getId()
        );
    }

    public Notification notifyPublicationBlock(
            User recipient, Long publicationId
    ) {
        return createNotification(
                recipient,
                "Publicação bloqueada",
                "Uma de suas publicações foi bloqueada.",
                NotificationType.PUBLICATION_BLOCK,
                "PUBLICATION",
                publicationId
        );
    }

    public Notification notifyPublicationUnblock(
            User recipient, Long publicationId
    ) {
        return createNotification(
                recipient,
                "Publicação desbloqueada",
                "Uma de suas publicações foi desbloqueada.",
                NotificationType.PUBLICATION_UNBLOCK,
                "PUBLICATION",
                publicationId
        );
    }

    public Notification notifyCollaborationInvite(
            User recipient, Long invitationId
    ) {
        return createNotification(
                recipient,
                "Convite de colaboração",
                "Você recebeu um novo convite para colaborar em uma publicação.",
                NotificationType.COLLABORATION_INVITE,
                "COLLABORATION_INVITE",
                invitationId
        );
    }

    public Notification notifyInviteResponse(
            User recipient, Long invitationId
    ) {
        return createNotification(
                recipient,
                "Resposta ao convite",
                "Seu convite de colaboração recebeu uma resposta.",
                NotificationType.INVITE_RESPONSE,
                "COLLABORATION_INVITE",
                invitationId
        );
    }

    // Verificar se a notificação pertence ao usuário
    private Notification getUserNotification(
            Long notificationId,
            Long userId
    ) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notificação não encontrada.")
                );

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new RuntimeException(
                    "Você não tem permissão para acessar esta notificação."
            );
        }

        return notification;
    }
}