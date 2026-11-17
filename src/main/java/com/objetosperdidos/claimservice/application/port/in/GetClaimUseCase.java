package com.objetosperdidos.claimservice.application.port.in;

import com.objetosperdidos.claimservice.domain.model.Claim;

import java.util.List;

public interface GetClaimUseCase {
    Claim byId(String id);
    List<Claim> byClaimant(String claimantId);
    List<Claim> byReport(String reportId);
}