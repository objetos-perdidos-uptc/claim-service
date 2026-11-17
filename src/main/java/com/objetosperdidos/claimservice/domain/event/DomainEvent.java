package com.objetosperdidos.claimservice.domain.event;

import java.time.Instant;

public interface DomainEvent {
    String claimId();
    Instant occurredAt();
}