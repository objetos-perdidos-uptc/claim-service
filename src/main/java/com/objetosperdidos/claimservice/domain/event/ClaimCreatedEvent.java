package com.objetosperdidos.claimservice.domain.event;

import java.time.Instant;

public record ClaimCreatedEvent(String claimId, String reportId,
                                String claimantId, String phone,
                                Instant occurredAt) implements DomainEvent {}