package com.objetosperdidos.claimservice.application.dto;

import com.objetosperdidos.claimservice.domain.model.ClaimStatus;

public record ResolveClaimCommand(String claimId, ClaimStatus status) {}