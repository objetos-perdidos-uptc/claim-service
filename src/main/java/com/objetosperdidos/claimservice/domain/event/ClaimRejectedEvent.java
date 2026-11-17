package com.objetosperdidos.claimservice.domain.event;

import java.time.Instant;

public record ClaimRejectedEvent(String claimId, Instant occurredAt) implements DomainEvent {}