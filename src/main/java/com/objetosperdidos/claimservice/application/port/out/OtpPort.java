package com.objetosperdidos.claimservice.application.port.out;

public interface OtpPort {

    enum VerificationResult { VALID, INVALID, EXPIRED, TOO_MANY_ATTEMPTS }

    String generate(String claimId);

    VerificationResult verify(String claimId, String code);
}