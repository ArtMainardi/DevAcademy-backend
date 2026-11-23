package com.example.trabalho.collaboration.service;

import com.example.trabalho.collaboration.enums.InviteStatus;
import com.example.trabalho.collaboration.model.CollaborationInvite;
import com.example.trabalho.collaboration.model.ContentCollaborator;
import com.example.trabalho.collaboration.repository.CollaborationInviteRepository;
import com.example.trabalho.collaboration.repository.ContentCollaboratorRepository;
import com.example.trabalho.content.model.Content;
import com.example.trabalho.content.repository.ContentRepository;
import com.example.trabalho.user.model.User;
import com.example.trabalho.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaborationService {

    private final CollaborationInviteRepository inviteRepository;
    private final ContentCollaboratorRepository collaboratorRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CollaborationInvite sendInvite(Long contentId, Long senderId, Long recipientId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado."));

        if (!content.getOwner().getId().equals(senderId)) {
            throw new AccessDeniedException("Apenas o proprietário do documento pode enviar convites de colaboração.");
        }

        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("O proprietário não pode convidar a si mesmo.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Remetente não encontrado."));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário destinatário não encontrado."));

        if (collaboratorRepository.existsByContentIdAndUserIdAndActiveTrue(contentId, recipientId)) {
            throw new IllegalStateException("Este usuário já é um colaborador ativo deste documento.");
        }
        if (inviteRepository.existsByContentIdAndRecipientIdAndStatus(contentId, recipientId, InviteStatus.PENDENTE)) {
            throw new IllegalStateException("Já existe um convite pendente para este usuário neste documento.");
        }

        CollaborationInvite invite = CollaborationInvite.builder()
                .content(content)
                .sender(sender)
                .recipient(recipient)
                .status(InviteStatus.PENDENTE)
                .build();

        return inviteRepository.save(invite);
    }

    @Transactional(readOnly = true)
    public List<CollaborationInvite> getReceivedInvites(Long userId) {
        return inviteRepository.findByRecipientIdOrderBySentAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<CollaborationInvite> getSentInvites(Long userId) {
        return inviteRepository.findBySenderIdOrderBySentAtDesc(userId);
    }

    @Transactional
    public CollaborationInvite acceptInvite(Long inviteId, Long recipientId) {
        CollaborationInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new EntityNotFoundException("Convite não encontrado."));

        if (!invite.getRecipient().getId().equals(recipientId)) {
            throw new AccessDeniedException("Apenas o destinatário pode aceitar este convite.");
        }

        if (collaboratorRepository.existsByContentIdAndUserIdAndActiveTrue(
                invite.getContent().getId(), recipientId)) {
            throw new IllegalStateException("Usuário já possui colaboração ativa neste documento.");
        }

        invite.accept();
        CollaborationInvite savedInvite = inviteRepository.save(invite);

        ContentCollaborator collaborator = ContentCollaborator.builder()
                .content(invite.getContent())
                .user(invite.getRecipient())
                .active(true)
                .build();
        collaboratorRepository.save(collaborator);

        return savedInvite;
    }

    @Transactional
    public CollaborationInvite rejectInvite(Long inviteId, Long recipientId) {
        CollaborationInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new EntityNotFoundException("Convite não encontrado."));

        if (!invite.getRecipient().getId().equals(recipientId)) {
            throw new AccessDeniedException("Apenas o destinatário pode recusar este convite.");
        }

        invite.reject();
        return inviteRepository.save(invite);
    }

    @Transactional
    public CollaborationInvite cancelInvite(Long inviteId, Long senderId) {
        CollaborationInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new EntityNotFoundException("Convite não encontrado."));

        if (!invite.getSender().getId().equals(senderId)) {
            throw new AccessDeniedException("Apenas o proprietário que enviou o convite pode cancelá-lo.");
        }

        invite.cancel();
        return inviteRepository.save(invite);
    }

    @Transactional(readOnly = true)
    public List<ContentCollaborator> listCollaborators(Long contentId) {
        return collaboratorRepository.findByContentIdAndActiveTrue(contentId);
    }

    @Transactional
    public void removeCollaborator(Long contentId, Long collaboratorUserId, Long ownerId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado."));

        if (!content.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Apenas o proprietário pode remover colaboradores.");
        }

        ContentCollaborator collaborator = collaboratorRepository
                .findByContentIdAndUserIdAndActiveTrue(contentId, collaboratorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador ativo não encontrado neste documento."));

        collaborator.setActive(false);
        collaboratorRepository.save(collaborator);
    }
}