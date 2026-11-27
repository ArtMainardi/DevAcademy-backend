package com.example.trabalho.hashtag.repository;

import com.example.trabalho.hashtag.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByNormalizedName(String normalizedName);

    boolean existsByNormalizedName(String normalizedName);

    List<Hashtag> findByNormalizedNameContainingIgnoreCase(String partialName);

    List<Hashtag> findAllByOrderByNormalizedNameAsc();
}