package com.example.trabalho;

import com.seuprojeto.model.enums.CorrectionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "correction_histories")
@Getter
@Setter
@NoArgsConstructor
public class CorrectionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrectionStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrectionStatus newStatus;

    @Lob
    private String justification;

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "correction_request_id", nullable = false)
    private CorrectionRequest correctionRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsible_user_id", nullable = false)
    private User responsibleUser;

    @PrePersist
    protected void onCreate() {
        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }

    public CorrectionHistory(
            CorrectionRequest correctionRequest,
            User responsibleUser,
            CorrectionStatus previousStatus,
            CorrectionStatus newStatus,
            String justification) {

        if (correctionRequest == null) {
            throw new IllegalArgumentException(
                    "A solicitação de correção é obrigatória."
            );
        }

        if (responsibleUser == null) {
            throw new IllegalArgumentException(
                    "O usuário responsável é obrigatório."
            );
        }

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "O novo status é obrigatório."
            );
        }

        this.correctionRequest = correctionRequest;
        this.responsibleUser = responsibleUser;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.justification = justification;
        this.changedAt = LocalDateTime.now();
    }
}