package com.example.trabalho.collaboration.controller;

import com.example.trabalho.collaboration.dto.CollaboratorResponseDTO;
import com.example.trabalho.collaboration.dto.InviteResponseDTO;
import com.example.trabalho.collaboration.model.CollaborationInvite;
import com.example.trabalho.collaboration.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collaborations")
@RequiredArgsConstructor
public class CollaborationController {

    private final CollaborationService collaborationService;

    @PostMapping("/invites")
    public ResponseEntity<InviteResponseDTO> sendInvite(
            @RequestParam Long contentId,
            @RequestParam Long recipientId,
            @RequestHeader("X-User-Id") Long senderId) {
        CollaborationInvite invite = collaborationService.sendInvite(contentId, senderId, recipientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(InviteResponseDTO.fromEntity(invite));
    }

    @GetMapping("/invites/received")
    public ResponseEntity<List<InviteResponseDTO>> getReceivedInvites(@RequestHeader("X-User-Id") Long userId) {
        List<InviteResponseDTO> list = collaborationService.getReceivedInvites(userId).stream()
                .map(InviteResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/invites/sent")
    public ResponseEntity<List<InviteResponseDTO>> getSentInvites(@RequestHeader("X-User-Id") Long userId) {
        List<InviteResponseDTO> list = collaborationService.getSentInvites(userId).stream()
                .map(InviteResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/invites/{inviteId}/accept")
    public ResponseEntity<InviteResponseDTO> acceptInvite(
            @PathVariable Long inviteId,
            @RequestHeader("X-User-Id") Long recipientId) {
        CollaborationInvite invite = collaborationService.acceptInvite(inviteId, recipientId);
        return ResponseEntity.ok(InviteResponseDTO.fromEntity(invite));
    }

    @PatchMapping("/invites/{inviteId}/reject")
    public ResponseEntity<InviteResponseDTO> rejectInvite(
            @PathVariable Long inviteId,
            @RequestHeader("X-User-Id") Long recipientId) {
        CollaborationInvite invite = collaborationService.rejectInvite(inviteId, recipientId);
        return ResponseEntity.ok(InviteResponseDTO.fromEntity(invite));
    }

    @PatchMapping("/invites/{inviteId}/cancel")
    public ResponseEntity<InviteResponseDTO> cancelInvite(
            @PathVariable Long inviteId,
            @RequestHeader("X-User-Id") Long senderId) {
        CollaborationInvite invite = collaborationService.cancelInvite(inviteId, senderId);
        return ResponseEntity.ok(InviteResponseDTO.fromEntity(invite));
    }

    @GetMapping("/contents/{contentId}/collaborators")
    public ResponseEntity<List<CollaboratorResponseDTO>> listCollaborators(@PathVariable Long contentId) {
        List<CollaboratorResponseDTO> list = collaborationService.listCollaborators(contentId).stream()
                .map(CollaboratorResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/contents/{contentId}/collaborators/{collaboratorUserId}")
    public ResponseEntity<Void> removeCollaborator(
            @PathVariable Long contentId,
            @PathVariable Long collaboratorUserId,
            @RequestHeader("X-User-Id") Long ownerId) {
        collaborationService.removeCollaborator(contentId, collaboratorUserId, ownerId);
        return ResponseEntity.noContent().build();
    }
}