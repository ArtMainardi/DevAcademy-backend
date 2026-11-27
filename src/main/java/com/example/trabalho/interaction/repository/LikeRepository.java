package com.example.trabalho.interaction.repository;

import com.example.trabalho.interaction.module.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Verifica se um usuário curtiu determinado documento
    boolean existsByUserIdAndContentId(Long userId, Long contentId);

    // Conta a quantidade de curtidas de um documento
    long countByContentId(Long contentId);

    // Conta as curtidas dos documentos de determinado proprietário
    long countByContentUserId(Long userId);

    // Remove a curtida de um usuário em determinado documento
    void deleteByUserIdAndContentId(Long userId, Long contentId);
}
