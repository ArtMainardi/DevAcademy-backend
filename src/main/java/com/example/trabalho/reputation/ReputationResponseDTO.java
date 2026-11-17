package com.example.trabalho.reputation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReputationResponseDTO {
    private Long userId;

    private Long totalLikes;

    private Long pendingCorrectionRequests;

    private Double averageRating;

    private Integer ratedDocuments;
}