package com.example.trabalho.collaboration.dto;

import com.example.trabalho.collaboration.enums.InviteStatus;
import com.example.trabalho.collaboration.model.CollaborationInvite;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class InviteResponseDTO {
    private Long id;
    private Long contentId;
    private String contentTitle;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private String recipientName;
    private InviteStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime respondedAt;

    public static InviteResponseDTO fromEntity(CollaborationInvite invite) {
        return InviteResponseDTO.builder()
                .id(invite.getId())
                .contentId(invite.getContent().getId())
                .contentTitle(invite.getContent().getTitle())
                .senderId(invite.getSender().getId())
                .senderName(invite.getSender().getName())
                .recipientId(invite.getRecipient().getId())
                .recipientName(invite.getRecipient().getName())
                .status(invite.getStatus())
                .sentAt(invite.getSentAt())
                .respondedAt(invite.getRespondedAt())
                .build();
    }
}