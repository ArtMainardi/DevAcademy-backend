package com.example.trabalho;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.exception.BusinessRuleException;
import com.example.app.exception.ForbiddenException;
import com.example.app.exception.NotFoundException;
import com.example.app.model.CorrectionHistory;
import com.example.app.model.CorrectionRequest;
import com.example.app.model.CorrectionStatus;
import com.example.app.model.Document;
import com.example.app.model.User;
import com.example.app.repository.CorrectionHistoryRepository;
import com.example.app.repository.CorrectionRequestRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.UserRepository;

/**
 * Regras de negócio das solicitações de correção de documentos.
 *
 * <p>Ciclo de vida:
 * <pre>
 *   PENDENTE ──aceitar──▶ ACEITO ──concluir──▶ CONCLUIDO
 *      │  ▲                 │  ▲
 *      │  └────retomar──────┤  │
 *      ├──adiar──▶ ADIADO ──┘  │
 *      │             │         │
 *      │             └─retomar─┘ (volta a PENDENTE)
 *      └──recusar──▶ RECUSADO
 * </pre>
 *
 * <p>Permissões:
 * <ul>
 *   <li>Somente o proprietário do documento gerencia (aceita, adia, recusa, retoma, conclui).</li>
 *   <li>O solicitante pode consultar a solicitação e o histórico para acompanhar o andamento.</li>
 * </ul>
 *
 * <p>Pendências: PENDENTE, ADIADO e ACEITO. RECUSADO e CONCLUIDO não contam.
 */
@Service
public class CorrectionService {

    /** Estados que contam como pendência para o proprietário. */
    public static final Set<CorrectionStatus> PENDING_STATUSES = EnumSet.of(
            CorrectionStatus.PENDENTE,
            CorrectionStatus.ADIADO,
            CorrectionStatus.ACEITO);

    /** Transições permitidas: estado atual -> estados de destino possíveis. */
    private static final Map<CorrectionStatus, Set<CorrectionStatus>> ALLOWED_TRANSITIONS = Map.of(
            CorrectionStatus.PENDENTE, EnumSet.of(
                    CorrectionStatus.ACEITO, CorrectionStatus.ADIADO, CorrectionStatus.RECUSADO),
            CorrectionStatus.ADIADO, EnumSet.of(
                    CorrectionStatus.ACEITO, CorrectionStatus.RECUSADO, CorrectionStatus.PENDENTE),
            CorrectionStatus.ACEITO, EnumSet.of(
                    CorrectionStatus.CONCLUIDO, CorrectionStatus.ADIADO, CorrectionStatus.PENDENTE),
            CorrectionStatus.RECUSADO, EnumSet.noneOf(CorrectionStatus.class),
            CorrectionStatus.CONCLUIDO, EnumSet.noneOf(CorrectionStatus.class));

    private final CorrectionRequestRepository requestRepository;
    private final CorrectionHistoryRepository historyRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public CorrectionService(CorrectionRequestRepository requestRepository,
                             CorrectionHistoryRepository historyRepository,
                             DocumentRepository documentRepository,
                             UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.historyRepository = historyRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    // ------------------------------------------------------------------
    // Criação
    // ------------------------------------------------------------------

    /**
     * Cria uma solicitação de correção em estado PENDENTE.
     * O proprietário do documento não pode solicitar correção do próprio documento.
     */
    @Transactional
    public CorrectionRequest createRequest(Long documentId, Long requesterId, String description) {
        if (description == null || description.isBlank()) {
            throw new BusinessRuleException("A descrição da correção é obrigatória.");
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Documento não encontrado: " + documentId));
        User requester = findUser(requesterId);

        if (document.getOwner().getId().equals(requesterId)) {
            throw new BusinessRuleException(
                    "O proprietário não pode solicitar correção do próprio documento.");
        }

        CorrectionRequest request = new CorrectionRequest();
        request.setDocument(document);
        request.setRequester(requester);
        request.setDescription(description.trim());
        request.setStatus(CorrectionStatus.PENDENTE);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(request.getCreatedAt());

        CorrectionRequest saved = requestRepository.save(request);
        recordHistory(saved, null, CorrectionStatus.PENDENTE, requester, "Solicitação criada.");
        return saved;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /** Consulta uma solicitação. Acesso: proprietário ou solicitante. */
    @Transactional(readOnly = true)
    public CorrectionRequest getRequest(Long requestId, Long userId) {
        CorrectionRequest request = findRequest(requestId);
        assertCanView(request, userId);
        return request;
    }

    /**
     * Lista as solicitações recebidas pelo proprietário (documentos dele).
     * Se {@code status} for nulo, retorna todas.
     */
    @Transactional(readOnly = true)
    public List<CorrectionRequest> listByOwner(Long ownerId, CorrectionStatus status) {
        return status == null
                ? requestRepository.findByDocumentOwnerIdOrderByCreatedAtDesc(ownerId)
                : requestRepository.findByDocumentOwnerIdAndStatusOrderByCreatedAtDesc(ownerId, status);
    }

    /** Lista as solicitações feitas pelo próprio solicitante (acompanhamento). */
    @Transactional(readOnly = true)
    public List<CorrectionRequest> listByRequester(Long requesterId) {
        return requestRepository.findByRequesterIdOrderByCreatedAtDesc(requesterId);
    }

    /** Histórico de mudanças de estado. Acesso: proprietário ou solicitante. */
    @Transactional(readOnly = true)
    public List<CorrectionHistory> getHistory(Long requestId, Long userId) {
        CorrectionRequest request = findRequest(requestId);
        assertCanView(request, userId);
        return historyRepository.findByCorrectionRequestIdOrderByChangedAtAsc(requestId);
    }

    /**
     * Conta as pendências (PENDENTE, ADIADO e ACEITO) considerando somente
     * solicitações de documentos de propriedade do usuário.
     */
    @Transactional(readOnly = true)
    public long countPendingByOwner(Long ownerId) {
        return requestRepository.countByDocumentOwnerIdAndStatusIn(ownerId, PENDING_STATUSES);
    }

    // ------------------------------------------------------------------
    // Gestão (somente proprietário)
    // ------------------------------------------------------------------

    @Transactional
    public CorrectionRequest accept(Long requestId, Long ownerId, String note) {
        return transition(requestId, ownerId, CorrectionStatus.ACEITO, note);
    }

    @Transactional
    public CorrectionRequest postpone(Long requestId, Long ownerId, String note) {
        return transition(requestId, ownerId, CorrectionStatus.ADIADO, note);
    }

    /** A recusa exige justificativa, para que o solicitante entenda o motivo. */
    @Transactional
    public CorrectionRequest reject(Long requestId, Long ownerId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessRuleException("O motivo da recusa é obrigatório.");
        }
        return transition(requestId, ownerId, CorrectionStatus.RECUSADO, reason);
    }

    /**
     * Retoma uma solicitação ADIADA ou ACEITA, devolvendo-a para PENDENTE
     * para nova análise.
     */
    @Transactional
    public CorrectionRequest resume(Long requestId, Long ownerId, String note) {
        CorrectionRequest request = findRequest(requestId);
        assertIsOwner(request, ownerId);

        CorrectionStatus current = request.getStatus();
        if (current != CorrectionStatus.ADIADO && current != CorrectionStatus.ACEITO) {
            throw new BusinessRuleException(
                    "Somente solicitações adiadas ou aceitas podem ser retomadas. Estado atual: " + current);
        }
        return applyTransition(request, ownerId, CorrectionStatus.PENDENTE, note);
    }

    /** Conclui a correção. Só é possível a partir de ACEITO. */
    @Transactional
    public CorrectionRequest complete(Long requestId, Long ownerId, String note) {
        return transition(requestId, ownerId, CorrectionStatus.CONCLUIDO, note);
    }

    // ------------------------------------------------------------------
    // Internos
    // ------------------------------------------------------------------

    private CorrectionRequest transition(Long requestId, Long ownerId,
                                         CorrectionStatus target, String note) {
        CorrectionRequest request = findRequest(requestId);
        assertIsOwner(request, ownerId);
        return applyTransition(request, ownerId, target, note);
    }

    private CorrectionRequest applyTransition(CorrectionRequest request, Long actorId,
                                              CorrectionStatus target, String note) {
        CorrectionStatus previous = request.getStatus();

        if (!ALLOWED_TRANSITIONS.getOrDefault(previous, Set.of()).contains(target)) {
            throw new BusinessRuleException(
                    "Transição inválida: " + previous + " -> " + target + ".");
        }

        request.setStatus(target);
        request.setUpdatedAt(LocalDateTime.now());
        CorrectionRequest saved = requestRepository.save(request);

        recordHistory(saved, previous, target, findUser(actorId), note);
        return saved;
    }

    private void recordHistory(CorrectionRequest request, CorrectionStatus previous,
                               CorrectionStatus next, User actor, String note) {
        CorrectionHistory history = new CorrectionHistory();
        history.setCorrectionRequest(request);
        history.setPreviousStatus(previous);
        history.setNewStatus(next);
        history.setChangedBy(actor);
        history.setChangedAt(LocalDateTime.now());
        history.setNote(note == null || note.isBlank() ? null : note.trim());
        historyRepository.save(history);
    }

    private CorrectionRequest findRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitação não encontrada: " + requestId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + userId));
    }

    private boolean isOwner(CorrectionRequest request, Long userId) {
        return request.getDocument().getOwner().getId().equals(userId);
    }

    private boolean isRequester(CorrectionRequest request, Long userId) {
        return request.getRequester().getId().equals(userId);
    }

    private void assertIsOwner(CorrectionRequest request, Long userId) {
        if (!isOwner(request, userId)) {
            throw new ForbiddenException(
                    "Somente o proprietário do documento pode gerenciar esta solicitação.");
        }
    }

    private void assertCanView(CorrectionRequest request, Long userId) {
        if (!isOwner(request, userId) && !isRequester(request, userId)) {
            throw new ForbiddenException("Você não tem acesso a esta solicitação.");
        }
    }
}