package com.example.trabalho;

import com.seuprojeto.model.enums.CorrectionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "correction_requests")
@Getter
@Setter
@NoArgsConstructor
public class CorrectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String description;

    @Lob
    @Column(nullable = false)
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrectionStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @OneToMany(
            mappedBy = "correctionRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CorrectionHistory> histories = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void createRequest() {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição do problema é obrigatória."
            );
        }

        if (justification == null || justification.isBlank()) {
            throw new IllegalArgumentException(
                    "A justificativa é obrigatória."
            );
        }

        if (content == null || requester == null) {
            throw new IllegalArgumentException(
                    "O documento e o solicitante são obrigatórios."
            );
        }

        this.status = CorrectionStatus.PENDENTE;
    }

    public void accept(Long userId) {
        validateOwner(userId);
        validateStatus(CorrectionStatus.PENDENTE, CorrectionStatus.ADIADO);

        this.status = CorrectionStatus.ACEITO;
    }

    public void postpone(Long userId) {
        validateOwner(userId);
        validateStatus(CorrectionStatus.PENDENTE);

        this.status = CorrectionStatus.ADIADO;
    }

    public void reject(Long userId) {
        validateOwner(userId);
        validateStatus(CorrectionStatus.PENDENTE, CorrectionStatus.ADIADO);

        this.status = CorrectionStatus.RECUSADO;
    }

    public void complete(Long userId) {
        validateOwner(userId);
        validateStatus(CorrectionStatus.ACEITO);

        this.status = CorrectionStatus.CONCLUÍDO;
    }

    private void validateOwner(Long userId) {
        if (userId == null
                || content == null
                || content.getUser() == null
                || !content.getUser().getId().equals(userId)) {

            throw new SecurityException(
                    "Somente o proprietário do documento pode gerenciar a solicitação."
            );
        }
    }

    private void validateStatus(CorrectionStatus... allowedStatuses) {
        for (CorrectionStatus allowedStatus : allowedStatuses) {
            if (this.status == allowedStatus) {
                return;
            }
        }

        throw new IllegalStateException(
                "Não é possível realizar essa operação com o status atual: " + status
        );
    }
}