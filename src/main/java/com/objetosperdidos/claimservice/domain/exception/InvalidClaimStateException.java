package com.objetosperdidos.claimservice.domain.exception;

public class InvalidClaimStateException extends DomainException {
    public InvalidClaimStateException(String message) { super(message); }
}