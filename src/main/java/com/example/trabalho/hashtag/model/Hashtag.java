package com.example.trabalho.hashtag.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hashtags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "normalized_name", nullable = false, unique = true)
    private String normalizedName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.name != null) {
            this.normalizedName = normalize(this.name);
        }
    }

    public static String normalize(String rawName) {
        if (rawName == null) return "";
        String cleaned = rawName.trim().toLowerCase();
        return cleaned.startsWith("#") ? cleaned.substring(1) : cleaned;
    }
}