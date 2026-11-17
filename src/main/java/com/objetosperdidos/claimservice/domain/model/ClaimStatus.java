package com.objetosperdidos.claimservice.domain.model;

public enum ClaimStatus {
    PENDING_OTP,
    PENDING,
    APPROVED,
    REJECTED,
    FLAGGED,
    CANCELLED;
    
    public  boolean isResolvable(){
        return  this == PENDING || this == FLAGGED;
    }

    public  boolean isFinal(){
        return  this == APPROVED || this == REJECTED || this == CANCELLED;
    }
}
