package com.example.trabalho.interaction.module;

import com.example.trabalho.content.model.Content;
import com.example.trabalho.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_user")
    )
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_content")
    )
    private Content content;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Atualiza o texto do comentário
    public void updateText(String newText) {
        if (newText == null || newText.isBlank()) {
            throw new IllegalArgumentException(
                    "O comentário não pode ser vazio."
            );
        }

        this.text = newText.trim();
    }

    // Verifica se o usuário é o autor do comentário
    public boolean isAuthor(Long userId) {
        return this.author != null
                && this.author.getId().equals(userId);
    }
}
