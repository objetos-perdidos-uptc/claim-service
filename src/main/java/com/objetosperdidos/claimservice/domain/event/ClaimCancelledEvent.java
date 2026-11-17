package com.objetosperdidos.claimservice.domain.event;

import java.time.Instant;

public record ClaimCancelledEvent(String claimId, String reason,
                                  Instant occurredAt) implements DomainEvent {}