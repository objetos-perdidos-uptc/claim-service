package com.objetosperdidos.claimservice.application.port.out;

import com.objetosperdidos.claimservice.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}