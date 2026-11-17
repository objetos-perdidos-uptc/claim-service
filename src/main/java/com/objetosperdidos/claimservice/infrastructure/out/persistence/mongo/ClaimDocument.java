package com.objetosperdidos.claimservice.infrastructure.out.persistence.mongo;

import com.objetosperdidos.claimservice.domain.model.ClaimStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "claims")
public class ClaimDocument {

    @Id private String id;
    @Indexed private String reportId;
    @Indexed private String claimantId;
    private String description;
    private String contactPhone;
    private ClaimStatus status;
    private int fraudScore;
    private String fraudReason;
    private Instant createdAt;
    private Instant resolvedAt;
    private String pickupCode;
    private String cancellationReason;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    public String getClaimantId() { return claimantId; }
    public void setClaimantId(String claimantId) { this.claimantId = claimantId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public int getFraudScore() { return fraudScore; }
    public void setFraudScore(int fraudScore) { this.fraudScore = fraudScore; }
    public String getFraudReason() { return fraudReason; }
    public void setFraudReason(String fraudReason) { this.fraudReason = fraudReason; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
    public String getPickupCode() { return pickupCode; }
    public void setPickupCode(String pickupCode) { this.pickupCode = pickupCode; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
}