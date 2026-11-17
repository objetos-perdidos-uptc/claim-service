package com.objetosperdidos.claimservice.application.port.in;

import com.objetosperdidos.claimservice.application.dto.ResolveClaimCommand;
import com.objetosperdidos.claimservice.domain.model.Claim;

public interface ResolveClaimUseCase {
    Claim resolve(ResolveClaimCommand cmd);
}