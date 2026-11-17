package com.objetosperdidos.claimservice.application.usecase;

import com.objetosperdidos.claimservice.application.dto.ResolveClaimCommand;
import com.objetosperdidos.claimservice.application.port.in.ResolveClaimUseCase;
import com.objetosperdidos.claimservice.application.saga.ClaimSaga;
import com.objetosperdidos.claimservice.domain.exception.ClaimNotFoundException;
import com.objetosperdidos.claimservice.domain.model.Claim;
import com.objetosperdidos.claimservice.domain.model.ClaimId;
import com.objetosperdidos.claimservice.domain.port.out.ClaimRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class ResolveClaimService implements ResolveClaimUseCase {

    private final ClaimRepositoryPort repo;
    private final ClaimSaga saga;

    public ResolveClaimService(ClaimRepositoryPort repo, ClaimSaga saga) {
        this.repo = repo;
        this.saga = saga;
    }

    @Override
    public Claim resolve(ResolveClaimCommand cmd) {
        Claim claim = repo.findById(ClaimId.of(cmd.claimId()))
                .orElseThrow(() -> new ClaimNotFoundException(cmd.claimId()));
        claim.resolve(cmd.status());     // el aggregate valida y genera pickupCode
        Claim saved = repo.save(claim);
        saga.onResolved(saved);
        return saved;
    }
}