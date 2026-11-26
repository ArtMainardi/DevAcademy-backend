package com.example.trabalho.collaboration.controller;

import com.example.trabalho.collaboration.dto.CollaboratorResponseDTO;
import com.example.trabalho.collaboration.dto.InviteResponseDTO;
import com.example.trabalho.collaboration.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborations")
@RequiredArgsConstructor
public class CollaborationController {

    private final CollaborationService collaborationService;

    @PostMapping("/invites")
    public ResponseEntity<InviteResponseDTO> sendInvite(
            @RequestParam Long contentId,
            @RequestParam Long recipientId,
            @RequestHeader("X-User-Id") Long senderId
    ) {
        InviteResponseDTO response = collaborationService.sendInvite(
                contentId,
                senderId,
                recipientId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/invites/received")
    public ResponseEntity<List<InviteResponseDTO>> getReceivedInvites(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(
                collaborationService.getReceivedInvites(userId)
        );
    }

    @GetMapping("/invites/sent")
    public ResponseEntity<List<InviteResponseDTO>> getSentInvites(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(
                collaborationService.getSentInvites(userId)
        );
    }

    @PatchMapping("/invites/{inviteId}/accept")
    public ResponseEntity<InviteResponseDTO> acceptInvite(
            @PathVariable Long inviteId,
            @RequestHeader("X-User-Id") Long recipientId
    ) {
        return ResponseEntity.ok(
                collaborationService.acceptInvite(inviteId, recipientId)
        );
    }

    @PatchMapping("/invites/{inviteId}/reject")
    public ResponseEntity<InviteResponseDTO> rejectInvite(
            @PathVariable Long inviteId,
            @RequestHeader("X-User-Id") Long recipientId
    ) {
        return ResponseEntity.ok(
                collaborationService.rejectInvite(inviteId, recipientId)
        );
    }

    @PatchMapping("/invites/{inviteId}/cancel")
    public ResponseEntity<InviteResponseDTO> cancelInvite(
            @PathVariable Long inviteId,
            @RequestHeader("X-User-Id") Long senderId
    ) {
        return ResponseEntity.ok(
                collaborationService.cancelInvite(inviteId, senderId)
        );
    }

    @GetMapping("/contents/{contentId}/collaborators")
    public ResponseEntity<List<CollaboratorResponseDTO>> listCollaborators(
            @PathVariable Long contentId
    ) {
        return ResponseEntity.ok(
                collaborationService.listCollaborators(contentId)
        );
    }

    @DeleteMapping("/contents/{contentId}/collaborators/{collaboratorUserId}")
    public ResponseEntity<Void> removeCollaborator(
            @PathVariable Long contentId,
            @PathVariable Long collaboratorUserId,
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        collaborationService.removeCollaborator(
                contentId,
                collaboratorUserId,
                ownerId
        );

        return ResponseEntity.noContent().build();
    }
}