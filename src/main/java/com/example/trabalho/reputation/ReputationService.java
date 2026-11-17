package com.example.trabalho.reputation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReputationService {
    private final ContentRepository contentRepository;
    private final LikeRepository likeRepository;
    private final ContentRatingRepository contentRatingRepository;
    private final CorrectionRequestRepository correctionRequestRepository;

    private static final Set<CorrectionStatus> PENDING_STATUSES = Set.of(
            CorrectionStatus.PENDENTE,
            CorrectionStatus.ADIADO,
            CorrectionStatus.ACEITO
    );

    @Transactional(readOnly = true)
    public ReputationResponseDTO calculateReputation(Long userId) {

        // Buscar somente documentos pertencentes ao usuário
        List<Content> contents = contentRepository.findByOwnerId(userId);

        // Sem documentos próprios, todos os indicadores serão zero
        if (contents.isEmpty()) {
            return ReputationResponseDTO.builder()
                    .userId(userId)
                    .totalLikes(0L)
                    .pendingCorrectionRequests(0L)
                    .averageRating(0.0)
                    .ratedDocuments(0)
                    .build();
        }

        // Indicador 1: total de curtidas recebidas
        long totalLikes = contents.stream()
                .mapToLong(content ->
                        likeRepository.countByContentId(content.getId())
                )
                .sum();

        // Indicador 2: solicitações de correção pendentes
        long pendingCorrections =
                correctionRequestRepository
                        .countByContentIdInAndStatusIn(
                                contents.stream()
                                        .map(Content::getId)
                                        .toList(),
                                PENDING_STATUSES
                        );

        // Indicador 3: média das médias individuais dos documentos
        double averageRating = 0.0;
        int ratedDocuments = 0;

        double totalDocumentAverages = 0.0;

        for (Content content : contents) {

            List<ContentRating> ratings =
                    contentRatingRepository.findByContentId(content.getId());

            // Desconsiderar documentos sem avaliações
            if (ratings.isEmpty()) {
                continue;
            }

            double documentAverage = ratings.stream()
                    .mapToInt(ContentRating::getRating)
                    .average()
                    .orElse(0.0);

            totalDocumentAverages += documentAverage;
            ratedDocuments++;
        }

        // Cada documento avaliado possui o mesmo peso
        if (ratedDocuments > 0) {
            averageRating = totalDocumentAverages / ratedDocuments;
        }

        return ReputationResponseDTO.builder()
                .userId(userId)
                .totalLikes(totalLikes)
                .pendingCorrectionRequests(pendingCorrections)
                .averageRating(Math.round(averageRating * 100.0) / 100.0)
                .ratedDocuments(ratedDocuments)
                .build();
    }
}