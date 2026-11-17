package com.objetosperdidos.claimservice.domain.event;

import java.time.Instant;

public record OtpVerifiedEvent(String claimId, Instant occurredAt) implements DomainEvent {}