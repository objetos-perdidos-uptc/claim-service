package com.objetosperdidos.claimservice.service;

import com.objetosperdidos.claimservice.dto.CreateClaimRequest;
import com.objetosperdidos.claimservice.model.Claim;
import com.objetosperdidos.claimservice.model.ClaimStatus;
import com.objetosperdidos.claimservice.repository.ClaimRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ClaimService {

    private static final Logger log = LoggerFactory.getLogger(ClaimService.class);

    private final ClaimRepository repo;
    private final OtpService otpService;
    private final FraudService fraudService;

    public ClaimService(ClaimRepository repo, OtpService otpService, FraudService fraudService) {
        this.repo = repo;
        this.otpService = otpService;
        this.fraudService = fraudService;
    }

    /** Crea el reclamo y genera el OTP. Devuelve el reclamo y el OTP (en producción se enviaría por SMS/email). */
    public Map<String, Object> create(String claimantId, CreateClaimRequest req) {
        if (repo.existsByReportIdAndClaimantId(req.reportId(), claimantId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya reclamaste este objeto");
        }

        var fraud = fraudService.evaluate(claimantId, req);

        Claim claim = repo.save(Claim.builder()
                .reportId(req.reportId())
                .claimantId(claimantId)
                .description(req.description().trim())
                .contactPhone(req.contactPhone())
                .status(ClaimStatus.PENDING_OTP)
                .fraudScore(fraud.score())
                .fraudReason(fraud.reason())
                .createdAt(Instant.now())
                .build());

        String otp = otpService.generate(claim.getId());
        // TODO (integración): llamar a notification-service para enviar el OTP al usuario.
        log.info("OTP para reclamo {}: {}", claim.getId(), otp);

        // devOtp solo para pruebas locales: quitar cuando notification-service esté integrado
        return Map.of("claim", claim, "devOtp", otp);
    }

    public Claim verifyOtp(String claimId, String claimantId, String code) {
        Claim claim = getOwned(claimId, claimantId);
        if (claim.getStatus() != ClaimStatus.PENDING_OTP) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El reclamo no está esperando OTP");
        }

        switch (otpService.verify(claimId, code)) {
            case VALID -> {
                boolean flagged = fraudService.isSuspicious(claim.getFraudScore());
                claim.setStatus(flagged ? ClaimStatus.FLAGGED : ClaimStatus.PENDING);
                return repo.save(claim);
            }
            case INVALID -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP incorrecto");
            case EXPIRED -> throw new ResponseStatusException(HttpStatus.GONE, "OTP expirado, solicita uno nuevo");
            case TOO_MANY_ATTEMPTS -> throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Demasiados intentos");
        }
        throw new IllegalStateException();
    }

    public Map<String, String> resendOtp(String claimId, String claimantId) {
        Claim claim = getOwned(claimId, claimantId);
        if (claim.getStatus() != ClaimStatus.PENDING_OTP) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El reclamo no está esperando OTP");
        }
        String otp = otpService.generate(claimId);
        log.info("OTP reenviado para reclamo {}: {}", claimId, otp);
        return Map.of("devOtp", otp);
    }

    public List<Claim> mine(String claimantId) { return repo.findByClaimantId(claimantId); }

    public List<Claim> byReport(String reportId) { return repo.findByReportId(reportId); }

    public Claim get(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Reclamo no encontrado"));
    }

    public Claim resolve(String id, ClaimStatus status) {
        if (status != ClaimStatus.APPROVED && status != ClaimStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo APPROVED o REJECTED");
        }
        Claim claim = get(id);
        if (claim.getStatus() == ClaimStatus.PENDING_OTP) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El reclamo aún no validó el OTP");
        }
        claim.setStatus(status);
        claim.setResolvedAt(Instant.now());
        return repo.save(claim);
    }

    private Claim getOwned(String claimId, String claimantId) {
        Claim claim = get(claimId);
        if (!claim.getClaimantId().equals(claimantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No es tu reclamo");
        }
        return claim;
    }
}