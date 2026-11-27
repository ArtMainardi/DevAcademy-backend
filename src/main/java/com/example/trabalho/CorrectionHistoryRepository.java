package com.example.trabalho;

import com.seuprojeto.model.CorrectionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrectionHistoryRepository
        extends JpaRepository<CorrectionHistory, Long> {

    // Consultar histórico de uma solicitação, das alterações mais recentes
    // para as mais antigas.
    List<CorrectionHistory> findByCorrectionRequestIdOrderByChangedAtDesc(
            Long correctionRequestId
    );
}