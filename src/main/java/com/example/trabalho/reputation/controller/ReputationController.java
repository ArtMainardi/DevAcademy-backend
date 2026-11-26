package com.example.trabalho.reputation.controller;

import com.example.trabalho.reputation.dto.ReputationResponseDTO;
import com.example.trabalho.reputation.service.ReputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reputation")
@RequiredArgsConstructor
public class ReputationController {

    private final ReputationService reputationService;

    // Consultar reputação de um usuário
    @GetMapping("/{userId}")
    public ResponseEntity<ReputationResponseDTO> getUserReputation(
            @PathVariable Long userId
    ) {
        ReputationResponseDTO reputation =
                reputationService.calculateReputation(userId);

        return ResponseEntity.ok(reputation);
    }
}