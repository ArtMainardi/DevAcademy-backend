package com.example.trabalho.collaboration.model;

import com.example.trabalho.collaboration.enums.InviteStatus;
import com.example.trabalho.content.model.Content;
import com.example.trabalho.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "collaboration_invites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborationInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InviteStatus status = InviteStatus.PENDENTE;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = InviteStatus.PENDENTE;
        }
    }

    public void accept() {
        if (this.status != InviteStatus.PENDENTE) {
            throw new IllegalStateException("Somente convites pendentes podem ser aceitos.");
        }
        this.status = InviteStatus.ACEITO;
        this.respondedAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != InviteStatus.PENDENTE) {
            throw new IllegalStateException("Somente convites pendentes podem ser recusados.");
        }
        this.status = InviteStatus.RECUSADO;
        this.respondedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != InviteStatus.PENDENTE) {
            throw new IllegalStateException("Somente convites pendentes podem ser cancelados.");
        }
        this.status = InviteStatus.CANCELADO;
        this.respondedAt = LocalDateTime.now();
    }
}