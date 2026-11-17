package com.objetosperdidos.claimservice.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ClaimId(String value) {

    public ClaimId {
        Objects.requireNonNull(value, "ClaimId no puede ser null");
        if (value.isBlank()) throw new IllegalArgumentException("ClaimId no puede ser vacío");
    }

    public static ClaimId newId() { return new ClaimId(UUID.randomUUID().toString()); }
    public static ClaimId of(String value) { return new ClaimId(value); }

    @Override public String toString() { return value; }
}