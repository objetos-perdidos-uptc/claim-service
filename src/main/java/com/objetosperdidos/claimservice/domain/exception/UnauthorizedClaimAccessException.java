package com.objetosperdidos.claimservice.domain.exception;

public class UnauthorizedClaimAccessException extends DomainException {
    public UnauthorizedClaimAccessException(String claimId) {
        super("No tienes permiso sobre el reclamo " + claimId);
    }
}