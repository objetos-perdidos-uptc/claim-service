package com.objetosperdidos.claimservice.domain.exception;

public class DuplicateClaimException extends DomainException {
    public DuplicateClaimException(String reportId, String claimantId) {
        super("Ya existe un reclamo del usuario " + claimantId + " para el reporte " + reportId);
    }
}