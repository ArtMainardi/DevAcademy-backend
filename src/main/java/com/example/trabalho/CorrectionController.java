package com.example.trabalho;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.CorrectionActionDTO;
import com.example.app.dto.CorrectionHistoryResponseDTO;
import com.example.app.dto.CorrectionRequestResponseDTO;
import com.example.app.dto.CreateCorrectionRequestDTO;
import com.example.app.dto.PendingCorrectionsResponseDTO;
import com.example.app.dto.RejectCorrectionDTO;
import com.example.app.model.CorrectionRequest;
import com.example.app.model.CorrectionStatus;
import com.example.app.security.UserPrincipal;
import com.example.app.service.CorrectionService;

import jakarta.validation.Valid;

/**
 * Endpoints REST das solicitações de correção.
 *
 * <p>O usuário autenticado é sempre obtido do contexto de segurança, nunca do corpo
 * ou da URL. As regras de permissão (proprietário x solicitante) ficam no service.
 */
@RestController
@RequestMapping("/api/corrections")
public class CorrectionController {

    private final CorrectionService correctionService;

    public CorrectionController(CorrectionService correctionService) {
        this.correctionService = correctionService;
    }

    // ---------------------------- Criação ----------------------------

    /** Cria uma solicitação de correção. O solicitante é o usuário autenticado. */
    @PostMapping
    public ResponseEntity<CorrectionRequestResponseDTO> create(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateCorrectionRequestDTO body) {

        CorrectionRequest created = correctionService.createRequest(
                body.documentId(), user.getId(), body.description());

        return ResponseEntity
                .created(URI.create("/api/corrections/" + created.getId()))
                .body(CorrectionRequestResponseDTO.from(created));
    }

    // ---------------------------- Consultas ----------------------------

    /** Consulta uma solicitação (proprietário ou solicitante). */
    @GetMapping("/{id}")
    public CorrectionRequestResponseDTO get(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {

        return CorrectionRequestResponseDTO.from(correctionService.getRequest(id, user.getId()));
    }

    /** Solicitações recebidas pelo proprietário, com filtro opcional por estado. */
    @GetMapping("/received")
    public List<CorrectionRequestResponseDTO> listReceived(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) CorrectionStatus status) {

        return correctionService.listByOwner(user.getId(), status).stream()
                .map(CorrectionRequestResponseDTO::from)
                .toList();
    }

    /** Solicitações feitas pelo usuário, para acompanhamento do andamento. */
    @GetMapping("/sent")
    public List<CorrectionRequestResponseDTO> listSent(
            @AuthenticationPrincipal UserPrincipal user) {

        return correctionService.listByRequester(user.getId()).stream()
                .map(CorrectionRequestResponseDTO::from)
                .toList();
    }

    /** Histórico de mudanças de estado (proprietário ou solicitante). */
    @GetMapping("/{id}/history")
    public List<CorrectionHistoryResponseDTO> history(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {

        return correctionService.getHistory(id, user.getId()).stream()
                .map(CorrectionHistoryResponseDTO::from)
                .toList();
    }

    /** Quantidade de pendências (PENDENTE, ADIADO e ACEITO) dos documentos do usuário. */
    @GetMapping("/pending-count")
    public PendingCorrectionsResponseDTO pendingCount(
            @AuthenticationPrincipal UserPrincipal user) {

        return new PendingCorrectionsResponseDTO(correctionService.countPendingByOwner(user.getId()));
    }

    // ------------------- Gestão (somente proprietário) -------------------

    @PostMapping("/{id}/accept")
    public CorrectionRequestResponseDTO accept(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody(required = false) CorrectionActionDTO body) {

        return CorrectionRequestResponseDTO.from(
                correctionService.accept(id, user.getId(), noteOf(body)));
    }

    @PostMapping("/{id}/postpone")
    public CorrectionRequestResponseDTO postpone(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody(required = false) CorrectionActionDTO body) {

        return CorrectionRequestResponseDTO.from(
                correctionService.postpone(id, user.getId(), noteOf(body)));
    }

    @PostMapping("/{id}/reject")
    public CorrectionRequestResponseDTO reject(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody RejectCorrectionDTO body) {

        return CorrectionRequestResponseDTO.from(
                correctionService.reject(id, user.getId(), body.reason()));
    }

    /** Retoma uma solicitação adiada ou aceita (volta para PENDENTE). */
    @PostMapping("/{id}/resume")
    public CorrectionRequestResponseDTO resume(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody(required = false) CorrectionActionDTO body) {

        return CorrectionRequestResponseDTO.from(
                correctionService.resume(id, user.getId(), noteOf(body)));
    }

    @PostMapping("/{id}/complete")
    public CorrectionRequestResponseDTO complete(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody(required = false) CorrectionActionDTO body) {

        return CorrectionRequestResponseDTO.from(
                correctionService.complete(id, user.getId(), noteOf(body)));
    }

    private static String noteOf(CorrectionActionDTO body) {
        return body == null ? null : body.note();
    }
}