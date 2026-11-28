package com.example.trabalho.interaction.module;

import com.example.trabalho.content.model.Content;
import com.example.trabalho.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "content_ratings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_content_rating_user_content",
                        columnNames = {"user_id", "content_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_content_rating_user")
    )
    private User evaluator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_content_rating_content")
    )
    private Content content;

    // Define as datas de criação e atualização
    @PrePersist
    public void prePersist() {
        validateScore();

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    // Atualiza a data de modificação
    @PreUpdate
    public void preUpdate() {
        validateScore();
        this.updatedAt = LocalDateTime.now();
    }

    // Valida se a nota está entre 1 e 5
    private void validateScore() {
        if (score == null || score < 1 || score > 5) {
            throw new IllegalArgumentException(
                    "A nota deve estar entre 1 e 5."
            );
        }
    }

    // Atualiza a nota da avaliação
    public void updateScore(Integer newScore) {
        if (newScore == null || newScore < 1 || newScore > 5) {
            throw new IllegalArgumentException(
                    "A nota deve estar entre 1 e 5."
            );
        }

        this.score = newScore;
    }

    // Verifica se o usuário é o avaliador
    public boolean isEvaluator(Long userId) {
        return this.evaluator != null
                && this.evaluator.getId().equals(userId);
    }

    // Verifica se o avaliador é o proprietário do conteúdo
    public boolean isOwner() {
        return this.evaluator != null
                && this.content != null
                && this.content.getOwner() != null
                && this.evaluator.getId().equals(
                this.content.getOwner().getId()
        );
    }
}
