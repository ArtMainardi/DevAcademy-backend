package com.example.trabalho.collaboration.dto;

import com.example.trabalho.collaboration.model.ContentCollaborator;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CollaboratorResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long contentId;
    private LocalDateTime joinedAt;
    private Boolean active;

    public static CollaboratorResponseDTO fromEntity(ContentCollaborator collaborator) {
        return CollaboratorResponseDTO.builder()
                .id(collaborator.getId())
                .userId(collaborator.getUser().getId())
                .userName(collaborator.getUser().getName())
                .userEmail(collaborator.getUser().getEmail())
                .contentId(collaborator.getContent().getId())
                .joinedAt(collaborator.getJoinedAt())
                .active(collaborator.getActive())
                .build();
    }
}