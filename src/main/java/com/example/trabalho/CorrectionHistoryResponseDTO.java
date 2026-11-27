package com.example.trabalho;

import java.time.LocalDateTime;

import com.example.app.model.CorrectionHistory;
import com.example.app.model.CorrectionStatus;

/** Representação de um registro do histórico de uma solicitação. */
public record CorrectionHistoryResponseDTO(
        Long id,
        Long correctionRequestId,
        CorrectionStatus previousStatus,
        CorrectionStatus newStatus,
        Long changedById,
        LocalDateTime changedAt,
        String note) {

    public static CorrectionHistoryResponseDTO from(CorrectionHistory history) {
        return new CorrectionHistoryResponseDTO(
                history.getId(),
                history.getCorrectionRequest().getId(),
                history.getPreviousStatus(),
                history.getNewStatus(),
                history.getChangedBy().getId(),
                history.getChangedAt(),
                history.getNote());
    }
}
