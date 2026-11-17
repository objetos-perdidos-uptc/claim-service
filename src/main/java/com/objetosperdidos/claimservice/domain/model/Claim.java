package com.objetosperdidos.claimservice.domain.model;

import com.objetosperdidos.claimservice.domain.exception.InvalidClaimStateException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;

public class Claim {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final ClaimId id;
    private final String reportId;
    private final String claimantId;
    private final String description;
    private final String contactPhone;
    private final int fraudScore;
    private final String fraudReason;
    private final Instant createdAt;

    private ClaimStatus status;
    private Instant resolvedAt;
    private String pickupCode;
    private String cancellationReason;

    private Claim(ClaimId id, String reportId, String claimantId,
                  String description, String contactPhone,
                  int fraudScore, String fraudReason,
                  Instant createdAt, ClaimStatus status,
                  Instant resolvedAt, String pickupCode, String cancellationReason) {
        this.id = id;
        this.reportId = reportId;
        this.claimantId = claimantId;
        this.description = description;
        this.contactPhone = contactPhone;
        this.fraudScore = fraudScore;
        this.fraudReason = fraudReason;
        this.createdAt = createdAt;
        this.status = status;
        this.resolvedAt = resolvedAt;
        this.pickupCode = pickupCode;
        this.cancellationReason = cancellationReason;
    }

    public static Claim create(String reportId, String claimantId,
                               String description, String contactPhone,
                               FraudAssessment fraud) {
        return new Claim(
                ClaimId.newId(),
                reportId, claimantId,
                description.trim(), contactPhone,
                fraud.score(), fraud.reason(),
                Instant.now(),
                ClaimStatus.PENDING_OTP,
                null, null, null
        );
    }

    public static Claim rehydrate(ClaimId id, String reportId, String claimantId,
                                  String description, String contactPhone,
                                  int fraudScore, String fraudReason,
                                  Instant createdAt, ClaimStatus status,
                                  Instant resolvedAt, String pickupCode,
                                  String cancellationReason) {
        return new Claim(id, reportId, claimantId, description, contactPhone,
                fraudScore, fraudReason, createdAt, status, resolvedAt,
                pickupCode, cancellationReason);
    }


    public void markOtpVerified(int suspiciousThreshold) {
        if (status != ClaimStatus.PENDING_OTP) {
            throw new InvalidClaimStateException(
                    "El reclamo no está esperando OTP (actual: " + status + ")");
        }
        this.status = fraudScore >= suspiciousThreshold
                ? ClaimStatus.FLAGGED
                : ClaimStatus.PENDING;
    }

    public void resolve(ClaimStatus target) {
        Objects.requireNonNull(target, "target");
        if (target != ClaimStatus.APPROVED && target != ClaimStatus.REJECTED) {
            throw new InvalidClaimStateException("Solo APPROVED o REJECTED son válidos para resolver");
        }
        if (!status.isResolvable()) {
            throw new InvalidClaimStateException(
                    "No se puede resolver un reclamo en estado " + status);
        }
        this.status = target;
        this.resolvedAt = Instant.now();
        if (target == ClaimStatus.APPROVED) {
            this.pickupCode = generatePickupCode();
        }
    }

    public void cancel(String reason) {
        if (status.isFinal()) {
            throw new InvalidClaimStateException(
                    "No se puede cancelar un reclamo ya finalizado (" + status + ")");
        }
        this.status = ClaimStatus.CANCELLED;
        this.cancellationReason = reason;
    }

    private String generatePickupCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }



    public ClaimId getId() { return id; }
    public String getReportId() { return reportId; }
    public String getClaimantId() { return claimantId; }
    public String getDescription() { return description; }
    public String getContactPhone() { return contactPhone; }
    public ClaimStatus getStatus() { return status; }
    public int getFraudScore() { return fraudScore; }
    public String getFraudReason() { return fraudReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public String getPickupCode() { return pickupCode; }
    public String getCancellationReason() { return cancellationReason; }
}