package com.objetosperdidos.claimservice.infrastructure.in.web.dto;

import com.objetosperdidos.claimservice.domain.model.ClaimStatus;

public record ResolveClaimHttpRequest(ClaimStatus status) {}