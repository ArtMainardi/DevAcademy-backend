package com.example.trabalho.interaction.repository;

import com.example.trabalho.interaction.module.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Verifica se um usuário curtiu determinado documento
    boolean existsByUser_IdAndContent_Id(Long userId, Long contentId);

    // Conta as curtidas de um documento
    long countByContent_Id(Long contentId);

    // Conta as curtidas dos documentos de determinado proprietário
    long countByContent_Owner_Id(Long ownerId);

    // Remove a curtida de um usuário em determinado documento
    void deleteByUser_IdAndContent_Id(Long userId, Long contentId);
}