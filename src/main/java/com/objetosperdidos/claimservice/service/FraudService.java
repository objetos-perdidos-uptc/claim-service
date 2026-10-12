package com.objetosperdidos.claimservice.service;


import com.objetosperdidos.claimservice.dto.CreateClaimRequest;
import com.objetosperdidos.claimservice.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Detección de fraude por reglas simples. Devuelve un puntaje 0-100
 * y las razones. Si supera el umbral el reclamo queda FLAGGED.
 */
@Service
public class FraudService {

    public record FraudResult(int score, String reason, boolean suspicious) {}

    private final ClaimRepository repo;
    private final int maxClaimsPerDay;
    private final int minDescriptionLength;
    private final int threshold;

    public FraudService(ClaimRepository repo,
                        @Value("${claims.fraud.max-claims-per-day}") int maxClaimsPerDay,
                        @Value("${claims.fraud.min-description-length}") int minDescriptionLength,
                        @Value("${claims.fraud.suspicious-threshold}") int threshold) {
        this.repo = repo;
        this.maxClaimsPerDay = maxClaimsPerDay;
        this.minDescriptionLength = minDescriptionLength;
        this.threshold = threshold;
    }

    public boolean isSuspicious(int score) { return score >= threshold; }

    public FraudResult evaluate(String claimantId, CreateClaimRequest req) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        // Regla 1: demasiados reclamos en 24 h
        long last24h = repo.countByClaimantIdAndCreatedAtAfter(
                claimantId, Instant.now().minus(Duration.ofHours(24)));
        if (last24h >= maxClaimsPerDay) {
            score += 40;
            reasons.add("Demasiados reclamos en 24 horas (" + last24h + ")");
        }

        // Regla 2: descripción muy corta (no demuestra propiedad)
        String desc = req.description().trim();
        if (desc.length() < minDescriptionLength) {
            score += 30;
            reasons.add("Descripción demasiado corta");
        }

        // Regla 3: descripción repetitiva o genérica
        String lower = desc.toLowerCase();
        if (lower.matches("^(es mio|es mío|es mi objeto|mio|mío)[.!\\s]*$")) {
            score += 30;
            reasons.add("Descripción genérica sin detalles");
        }
        if (desc.chars().distinct().count() < 6) {
            score += 20;
            reasons.add("Descripción con caracteres repetidos");
        }

        // Regla 4: teléfono con formato inválido (Colombia: 10 dígitos)
        if (!req.contactPhone().replaceAll("[\\s+-]", "").matches("(57)?\\d{10}")) {
            score += 20;
            reasons.add("Teléfono con formato inválido");
        }

        score = Math.min(score, 100);
        return new FraudResult(score, String.join("; ", reasons), score >= threshold);
    }
}
