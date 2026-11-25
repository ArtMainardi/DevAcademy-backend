package com.example.trabalho.content.repository;

import com.example.trabalho.content.model.ContentChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentChangeRepository extends JpaRepository<ContentChange, Long> {

    List<ContentChange> findByContentIdOrderByChangedAtDesc(Long contentId);
}