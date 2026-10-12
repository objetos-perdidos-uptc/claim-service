package com.objetosperdidos.claimservice.dto;

import com.objetosperdidos.claimservice.model.ClaimStatus;

public record ResolveClaimRequest(ClaimStatus status) {}
