package com.objetosperdidos.claimservice.domain.exception;

public class NotificationFailedException extends DomainException {
    public NotificationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}