package com.objetosperdidos.claimservice.application.dto;

public record VerifyOtpCommand(String claimId, String claimantId, String code) {}