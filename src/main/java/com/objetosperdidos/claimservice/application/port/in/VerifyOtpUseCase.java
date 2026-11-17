package com.objetosperdidos.claimservice.application.port.in;

import com.objetosperdidos.claimservice.application.dto.VerifyOtpCommand;
import com.objetosperdidos.claimservice.domain.model.Claim;

public interface VerifyOtpUseCase {
    Claim verify(VerifyOtpCommand cmd);
}