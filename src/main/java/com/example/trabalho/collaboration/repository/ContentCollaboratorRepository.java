package com.example.trabalho.collaboration.repository;

import com.example.trabalho.collaboration.model.ContentCollaborator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentCollaboratorRepository extends JpaRepository<ContentCollaborator, Long> {

    List<ContentCollaborator> findByContentIdAndActiveTrue(Long contentId);

    Optional<ContentCollaborator> findByContentIdAndUserIdAndActiveTrue(Long contentId, Long userId);

    boolean existsByContentIdAndUserIdAndActiveTrue(Long contentId, Long userId);

    Page<ContentCollaborator> findByUserIdAndActiveTrue(Long userId, Pageable pageable);
}