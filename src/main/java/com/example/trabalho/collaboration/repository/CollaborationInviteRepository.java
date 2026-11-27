package com.example.trabalho.collaboration.repository;

import com.example.trabalho.collaboration.enums.InviteStatus;
import com.example.trabalho.collaboration.model.CollaborationInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaborationInviteRepository extends JpaRepository<CollaborationInvite, Long> {

    List<CollaborationInvite> findByRecipientIdOrderBySentAtDesc(Long recipientId);

    List<CollaborationInvite> findBySenderIdOrderBySentAtDesc(Long senderId);

    List<CollaborationInvite> findByRecipientIdAndStatusOrderBySentAtDesc(Long recipientId, InviteStatus status);

    boolean existsByContentIdAndRecipientIdAndStatus(Long contentId, Long recipientId, InviteStatus status);
}