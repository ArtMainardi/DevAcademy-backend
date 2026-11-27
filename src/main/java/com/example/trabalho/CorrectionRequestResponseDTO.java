package com.example.trabalho;
import java.time.LocalDateTime;

import com.example.app.model.CorrectionRequest;
import com.example.app.model.CorrectionStatus;

/** Representação de uma solicitação de correção na API. */
public record CorrectionRequestResponseDTO(
        Long id,
        Long documentId,
        Long ownerId,
        Long requesterId,
        String description,
        CorrectionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static CorrectionRequestResponseDTO from(CorrectionRequest request) {
        return new CorrectionRequestResponseDTO(
                request.getId(),
                request.getDocument().getId(),
                request.getDocument().getOwner().getId(),
                request.getRequester().getId(),
                request.getDescription(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt());
    }
}
