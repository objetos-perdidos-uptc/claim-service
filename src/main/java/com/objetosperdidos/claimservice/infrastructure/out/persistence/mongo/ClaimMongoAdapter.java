package com.objetosperdidos.claimservice.infrastructure.out.persistence.mongo;

import com.objetosperdidos.claimservice.domain.model.Claim;
import com.objetosperdidos.claimservice.domain.model.ClaimId;
import com.objetosperdidos.claimservice.domain.model.ClaimStatus;
import com.objetosperdidos.claimservice.domain.port.out.ClaimRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class ClaimMongoAdapter implements ClaimRepositoryPort {

    private final ClaimMongoRepository mongo;

    public ClaimMongoAdapter(ClaimMongoRepository mongo) { this.mongo = mongo; }

    @Override
    public Claim save(Claim claim) {
        ClaimDocument doc = toDocument(claim);
        ClaimDocument saved = mongo.save(doc);
        return toDomain(saved);
    }

    @Override
    public Optional<Claim> findById(ClaimId id) {
        return mongo.findById(id.value()).map(this::toDomain);
    }

    @Override public List<Claim> findByClaimantId(String c) {
        return mongo.findByClaimantId(c).stream().map(this::toDomain).toList();
    }
    @Override public List<Claim> findByReportId(String r) {
        return mongo.findByReportId(r).stream().map(this::toDomain).toList();
    }
    @Override public long countByStatus(ClaimStatus s) { return mongo.countByStatus(s); }
    @Override public long count() { return mongo.count(); }
    @Override public long countByClaimantIdSince(String c, Instant since) {
        return mongo.countByClaimantIdAndCreatedAtAfter(c, since);
    }
    @Override public boolean existsByReportIdAndClaimantId(String r, String c) {
        return mongo.existsByReportIdAndClaimantId(r, c);
    }

    private ClaimDocument toDocument(Claim c) {
        ClaimDocument d = new ClaimDocument();
        d.setId(c.getId().value());
        d.setReportId(c.getReportId());
        d.setClaimantId(c.getClaimantId());
        d.setDescription(c.getDescription());
        d.setContactPhone(c.getContactPhone());
        d.setStatus(c.getStatus());
        d.setFraudScore(c.getFraudScore());
        d.setFraudReason(c.getFraudReason());
        d.setCreatedAt(c.getCreatedAt());
        d.setResolvedAt(c.getResolvedAt());
        d.setPickupCode(c.getPickupCode());
        d.setCancellationReason(c.getCancellationReason());
        return d;
    }

    private Claim toDomain(ClaimDocument d) {
        return Claim.rehydrate(
                ClaimId.of(d.getId()), d.getReportId(), d.getClaimantId(),
                d.getDescription(), d.getContactPhone(),
                d.getFraudScore(), d.getFraudReason(),
                d.getCreatedAt(), d.getStatus(),
                d.getResolvedAt(), d.getPickupCode(), d.getCancellationReason());
    }
}