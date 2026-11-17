package com.objetosperdidos.claimservice.domain.exception;

public class ClaimNotFoundException extends DomainException {
    public ClaimNotFoundException(String claimId) {
        super("Reclamo no encontrado: " + claimId);
    }
}