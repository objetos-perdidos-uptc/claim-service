package com.objetosperdidos.claimservice.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.objetosperdidos.claimservice.model.Claim;
import com.objetosperdidos.claimservice.model.ClaimStatus;

public interface ClaimRepository extends MongoRepository<Claim, String>{
    List<Claim> findByClaimantId(String claimantId);
    List<Claim> findByReportId(String reportId);
    long countByStatus(ClaimStatus status);
    long countByClaimantIdAndCreatedAtAfter(String claimantId, Instant since);
    boolean existsByReportIdAndClaimantId(String reportId, String claimantId);
}
