package com.objetosperdidos.claimservice.application.usecase;

import com.objetosperdidos.claimservice.application.dto.CreateClaimCommand;
import com.objetosperdidos.claimservice.application.dto.CreateClaimResult;
import com.objetosperdidos.claimservice.application.port.in.CreateClaimUseCase;
import com.objetosperdidos.claimservice.application.port.out.FraudPort;
import com.objetosperdidos.claimservice.application.saga.ClaimSaga;
import com.objetosperdidos.claimservice.domain.exception.DuplicateClaimException;
import com.objetosperdidos.claimservice.domain.model.Claim;
import com.objetosperdidos.claimservice.domain.model.FraudAssessment;
import com.objetosperdidos.claimservice.domain.port.out.ClaimRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateClaimService implements CreateClaimUseCase {

    private final ClaimRepositoryPort repo;
    private final FraudPort fraud;
    private final ClaimSaga saga;

    public CreateClaimService(ClaimRepositoryPort repo, FraudPort fraud, ClaimSaga saga) {
        this.repo = repo;
        this.fraud = fraud;
        this.saga = saga;
    }

    @Override
    public CreateClaimResult create(String claimantId, CreateClaimCommand cmd) {
        if (repo.existsByReportIdAndClaimantId(cmd.reportId(), claimantId)) {
            throw new DuplicateClaimException(cmd.reportId(), claimantId);
        }

        FraudAssessment assessment = fraud.evaluate(
                claimantId, cmd.reportId(), cmd.description(), cmd.contactPhone());

        Claim claim = Claim.create(cmd.reportId(), claimantId,
                cmd.description(), cmd.contactPhone(), assessment);

        String otp = saga.startCreation(claim);

        return new CreateClaimResult(claim, otp);
    }
}