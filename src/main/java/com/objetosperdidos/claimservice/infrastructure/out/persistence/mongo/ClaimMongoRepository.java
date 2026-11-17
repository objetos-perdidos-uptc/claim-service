package com.objetosperdidos.claimservice.infrastructure.out.persistence.mongo;

import com.objetosperdidos.claimservice.domain.model.ClaimStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ClaimMongoRepository extends MongoRepository<ClaimDocument, String> {
    List<ClaimDocument> findByClaimantId(String claimantId);
    List<ClaimDocument> findByReportId(String reportId);
    long countByStatus(ClaimStatus status);
    long countByClaimantIdAndCreatedAtAfter(String claimantId, Instant since);
    boolean existsByReportIdAndClaimantId(String reportId, String claimantId);
}