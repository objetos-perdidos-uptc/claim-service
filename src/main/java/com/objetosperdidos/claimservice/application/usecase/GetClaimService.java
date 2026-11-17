package com.objetosperdidos.claimservice.application.usecase;

import com.objetosperdidos.claimservice.application.port.in.GetClaimUseCase;
import com.objetosperdidos.claimservice.domain.exception.ClaimNotFoundException;
import com.objetosperdidos.claimservice.domain.model.Claim;
import com.objetosperdidos.claimservice.domain.model.ClaimId;
import com.objetosperdidos.claimservice.domain.port.out.ClaimRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetClaimService implements GetClaimUseCase {

    private final ClaimRepositoryPort repo;

    public GetClaimService(ClaimRepositoryPort repo) { this.repo = repo; }

    @Override
    public Claim byId(String id) {
        return repo.findById(ClaimId.of(id))
                .orElseThrow(() -> new ClaimNotFoundException(id));
    }

    @Override public List<Claim> byClaimant(String claimantId) { return repo.findByClaimantId(claimantId); }
    @Override public List<Claim> byReport(String reportId)     { return repo.findByReportId(reportId); }
}