package com.example.trabalho.correction;

import com.seuprojeto.model.CorrectionRequest;
import com.seuprojeto.model.enums.CorrectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrectionRequestRepository
        extends JpaRepository<CorrectionRequest, Long> {

    // Listar solicitações de um documento
    List<CorrectionRequest> findByContentIdOrderByCreatedAtDesc(Long contentId);

    // Listar solicitações dos documentos de um proprietário
    List<CorrectionRequest> findByContentUserIdOrderByCreatedAtDesc(Long userId);

    // Contar solicitações pendentes (PENDENTE, ADIADO e ACEITO)
    @Query("""
        SELECT COUNT(cr)
        FROM CorrectionRequest cr
        WHERE cr.status IN :statuses
    """)
    long countPendingRequests(
            @Param("statuses") List<CorrectionStatus> statuses
    );

    // Buscar solicitações por estado
    List<CorrectionRequest> findByStatusOrderByCreatedAtDesc(
            CorrectionStatus status
    );

    // Consultar solicitações de determinado solicitante
    List<CorrectionRequest> findByRequesterIdOrderByCreatedAtDesc(
            Long requesterId
    );

    // Contar solicitações pendentes de um documento
    @Query("""
        SELECT COUNT(cr)
        FROM CorrectionRequest cr
        WHERE cr.content.id = :contentId
          AND cr.status IN :statuses
    """)
    long countPendingByContentId(
            @Param("contentId") Long contentId,
            @Param("statuses") List<CorrectionStatus> statuses
    );

    // Contar solicitações pendentes de um proprietário
    @Query("""
        SELECT COUNT(cr)
        FROM CorrectionRequest cr
        WHERE cr.content.user.id = :userId
          AND cr.status IN :statuses
    """)
    long countPendingByOwnerId(
            @Param("userId") Long userId,
            @Param("statuses") List<CorrectionStatus> statuses
    );
}