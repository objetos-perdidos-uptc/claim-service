package com.objetosperdidos.claimservice.domain.event;

import java.time.Instant;

public record OtpDispatchedEvent(String claimId, Instant occurredAt) implements DomainEvent {}