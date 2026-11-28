package com.example.trabalho.interaction.repository;

import com.example.trabalho.interaction.module.ContentRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRatingRepository extends JpaRepository<ContentRating, Long> {

    // Buscar avaliação de um usuário em determinado documento
    Optional<ContentRating> findByEvaluatorIdAndContentId(
            Long userId,
            Long contentId
    );

    // Calcular média das avaliações de um documento
    @Query("""
        SELECT AVG(cr.score)
        FROM ContentRating cr
        WHERE cr.content.id = :contentId
    """)
    Double calculateAverageByContentId(
            @Param("contentId") Long contentId
    );

    // Contar avaliações de um documento
    long countByContentId(Long contentId);

    // Listar avaliações de determinado documento
    List<ContentRating> findByContentIdOrderByCreatedAtDesc(
            Long contentId
    );
};
