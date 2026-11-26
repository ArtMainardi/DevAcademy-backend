package com.example.trabalho.user.repository;

import com.example.trabalho.user.model.ActivityHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityHistoryRepository extends JpaRepository<ActivityHistory, Long> {
    List<ActivityHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}