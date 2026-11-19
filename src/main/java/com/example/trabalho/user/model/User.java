package com.example.trabalho.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 1000)
    private String biography;

    private String profileImage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(
            String name,
            String biography,
            String profileImage
    ) {
        if (name != null) {
            this.name = name;
        }

        if (biography != null) {
            this.biography = biography;
        }

        if (profileImage != null) {
            this.profileImage = profileImage;
        }
    }

    public void changePassword(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "A senha criptografada não pode ser vazia."
            );
        }

        this.password = hashedPassword;
    }

    public void deactivateAccount() {
        this.active = false;
    }

    public void reactivateAccount() {
        this.active = true;
    }
}