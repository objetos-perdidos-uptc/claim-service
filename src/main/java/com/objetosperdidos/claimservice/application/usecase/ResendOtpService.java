package com.objetosperdidos.claimservice.application.usecase;

import com.objetosperdidos.claimservice.application.port.in.ResendOtpUseCase;
import com.objetosperdidos.claimservice.application.port.out.NotificationPort;
import com.objetosperdidos.claimservice.application.port.out.OtpPort;
import com.objetosperdidos.claimservice.domain.exception.ClaimNotFoundException;
import com.objetosperdidos.claimservice.domain.exception.InvalidClaimStateException;
import com.objetosperdidos.claimservice.domain.exception.UnauthorizedClaimAccessException;
import com.objetosperdidos.claimservice.domain.model.Claim;
import com.objetosperdidos.claimservice.domain.model.ClaimId;
import com.objetosperdidos.claimservice.domain.model.ClaimStatus;
import com.objetosperdidos.claimservice.domain.port.out.ClaimRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class ResendOtpService implements ResendOtpUseCase {

    private final ClaimRepositoryPort repo;
    private final OtpPort otp;
    private final NotificationPort notifier;

    public ResendOtpService(ClaimRepositoryPort repo, OtpPort otp, NotificationPort notifier) {
        this.repo = repo;
        this.otp = otp;
        this.notifier = notifier;
    }

    @Override
    public String resend(String claimId, String claimantId) {
        Claim claim = repo.findById(ClaimId.of(claimId))
                .orElseThrow(() -> new ClaimNotFoundException(claimId));
        if (!claim.getClaimantId().equals(claimantId))
            throw new UnauthorizedClaimAccessException(claimId);
        if (claim.getStatus() != ClaimStatus.PENDING_OTP)
            throw new InvalidClaimStateException("El reclamo no está esperando OTP");

        String code = otp.generate(claimId);
        notifier.sendOtp(claim.getContactPhone(), code);
        return code;
    }
}