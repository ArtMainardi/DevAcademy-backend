package com.example.trabalho.HashtagModule;

import jakarta.persistence.*;
import lombok.*;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(
        name = "hashtags",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hashtag_normalized_name",
                        columnNames = "normalized_name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(
            name = "normalized_name",
            nullable = false,
            unique = true,
            length = 100
    )
    private String normalizedName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    public void normalize() {
        if (name != null) {
            this.name = name.trim();

            this.normalizedName = Normalizer
                    .normalize(this.name, Normalizer.Form.NFKC)
                    .toLowerCase(Locale.ROOT);

            if (!this.normalizedName.startsWith("#")) {
                this.normalizedName = "#" + this.normalizedName;
            }
        }

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
