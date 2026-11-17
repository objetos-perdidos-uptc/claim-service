package com.objetosperdidos.claimservice.application.port.in;

import com.objetosperdidos.claimservice.application.dto.CreateClaimCommand;
import com.objetosperdidos.claimservice.application.dto.CreateClaimResult;

public interface CreateClaimUseCase {
    CreateClaimResult create(String claimantId, CreateClaimCommand cmd);
}