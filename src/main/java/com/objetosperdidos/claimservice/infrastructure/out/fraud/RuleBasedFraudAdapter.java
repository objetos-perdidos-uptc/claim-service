package com.objetosperdidos.claimservice.infrastructure.out.fraud;

import com.objetosperdidos.claimservice.application.port.out.FraudPort;
import com.objetosperdidos.claimservice.domain.model.FraudAssessment;
import com.objetosperdidos.claimservice.domain.port.out.ClaimRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class RuleBasedFraudAdapter implements FraudPort {

    private final ClaimRepositoryPort repo;
    private final int maxClaimsPerDay;
    private final int minDescriptionLength;
    private final int threshold;

    public RuleBasedFraudAdapter(ClaimRepositoryPort repo,
                                 @Value("${claims.fraud.max-claims-per-day}") int maxClaimsPerDay,
                                 @Value("${claims.fraud.min-description-length}") int minLength,
                                 @Value("${claims.fraud.suspicious-threshold}") int threshold) {
        this.repo = repo;
        this.maxClaimsPerDay = maxClaimsPerDay;
        this.minDescriptionLength = minLength;
        this.threshold = threshold;
    }

    @Override
    public FraudAssessment evaluate(String claimantId, String reportId,
                                    String description, String contactPhone) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        long last24h = repo.countByClaimantIdSince(claimantId,
                Instant.now().minus(Duration.ofHours(24)));
        if (last24h >= maxClaimsPerDay) {
            score += 40;
            reasons.add("Demasiados reclamos en 24h (" + last24h + ")");
        }

        String desc = description.trim();
        if (desc.length() < minDescriptionLength) {
            score += 30;
            reasons.add("Descripción demasiado corta");
        }

        String lower = desc.toLowerCase();
        if (lower.matches("^(es mio|es mío|es mi objeto|mio|mío)[.!\\s]*$")) {
            score += 30;
            reasons.add("Descripción genérica sin detalles");
        }
        if (desc.chars().distinct().count() < 6) {
            score += 20;
            reasons.add("Descripción con caracteres repetidos");
        }

        if (!contactPhone.replaceAll("[\\s+-]", "").matches("(57)?\\d{10}")) {
            score += 20;
            reasons.add("Teléfono con formato inválido");
        }

        score = Math.min(score, 100);
        return new FraudAssessment(score, String.join("; ", reasons), score >= threshold);
    }
}