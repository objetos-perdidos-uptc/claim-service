package com.objetosperdidos.claimservice.application.dto;

import com.objetosperdidos.claimservice.domain.model.Claim;

public record CreateClaimResult(Claim claim, String devOtp) {}