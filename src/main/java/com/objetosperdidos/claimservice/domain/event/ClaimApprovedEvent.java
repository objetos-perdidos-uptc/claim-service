package com.objetosperdidos.claimservice.domain.event;

import java.time.Instant;

public record ClaimApprovedEvent(String claimId, String pickupCode,
                                 Instant occurredAt) implements DomainEvent {}