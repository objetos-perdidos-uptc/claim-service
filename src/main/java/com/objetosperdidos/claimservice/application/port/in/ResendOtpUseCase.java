package com.objetosperdidos.claimservice.application.port.in;

public interface ResendOtpUseCase {
    String resend(String claimId, String claimantId);
}