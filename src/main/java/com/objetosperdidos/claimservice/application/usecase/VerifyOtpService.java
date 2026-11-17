package com.objetosperdidos.claimservice.application.usecase;

import com.objetosperdidos.claimservice.application.dto.VerifyOtpCommand;
import com.objetosperdidos.claimservice.application.port.in.VerifyOtpUseCase;
import com.objetosperdidos.claimservice.application.port.out.OtpPort;
import com.objetosperdidos.claimservice.application.saga.ClaimSaga;
import com.objetosperdidos.claimservice.domain.exception.InvalidClaimStateException;
import com.objetosperdidos.claimservice.domain.exception.UnauthorizedClaimAccessException;
import com.objetosperdidos.claimservice.domain.exception.ClaimNotFoundException;
import com.objetosperdidos.claimservice.domain.model.Claim;
import com.objetosperdidos.claimservice.domain.model.ClaimId;
import com.objetosperdidos.claimservice.domain.port.out.ClaimRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VerifyOtpService implements VerifyOtpUseCase {

    private final ClaimRepositoryPort repo;
    private final OtpPort otp;
    private final ClaimSaga saga;
    private final int suspiciousThreshold;

    public VerifyOtpService(ClaimRepositoryPort repo, OtpPort otp, ClaimSaga saga,
                            @Value("${claims.fraud.suspicious-threshold}") int threshold) {
        this.repo = repo;
        this.otp = otp;
        this.saga = saga;
        this.suspiciousThreshold = threshold;
    }

    @Override
    public Claim verify(VerifyOtpCommand cmd) {
        Claim claim = repo.findById(ClaimId.of(cmd.claimId()))
                .orElseThrow(() -> new ClaimNotFoundException(cmd.claimId()));

        if (!claim.getClaimantId().equals(cmd.claimantId()))
            throw new UnauthorizedClaimAccessException(cmd.claimId());

        if (claim.getStatus() != com.objetosperdidos.claimservice.domain.model.ClaimStatus.PENDING_OTP)
            throw new InvalidClaimStateException("El reclamo no está esperando OTP");

        OtpPort.VerificationResult result = otp.verify(cmd.claimId(), cmd.code());
        switch (result) {
            case INVALID -> throw new InvalidClaimStateException("OTP incorrecto");
            case EXPIRED -> throw new InvalidClaimStateException("OTP expirado");
            case TOO_MANY_ATTEMPTS -> throw new InvalidClaimStateException("Demasiados intentos");
            case VALID -> { /* sigue */ }
        }

        claim.markOtpVerified(suspiciousThreshold);
        saga.onOtpVerified(claim);
        return claim;
    }
}