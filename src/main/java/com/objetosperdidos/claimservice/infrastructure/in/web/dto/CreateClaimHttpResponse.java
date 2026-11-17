package com.objetosperdidos.claimservice.infrastructure.in.web.dto;

import com.objetosperdidos.claimservice.domain.model.Claim;

public record CreateClaimHttpResponse(Claim claim, String devOtp) {}