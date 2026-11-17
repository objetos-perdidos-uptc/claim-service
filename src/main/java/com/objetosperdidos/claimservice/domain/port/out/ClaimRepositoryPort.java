package com.objetosperdidos.claimservice.domain.port.out;

import com.objetosperdidos.claimservice.domain.model.Claim;
import com.objetosperdidos.claimservice.domain.model.ClaimId;
import com.objetosperdidos.claimservice.domain.model.ClaimStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ClaimRepositoryPort {
    Claim save(Claim claim);
    Optional<Claim> findById(ClaimId id);
    List<Claim> findByClaimantId(String claimantId);
    List<Claim> findByReportId(String reportId);
    long countByStatus(ClaimStatus status);
    long count();
    long countByClaimantIdSince(String claimantId, Instant since);
    boolean existsByReportIdAndClaimantId(String reportId, String claimantId);
}