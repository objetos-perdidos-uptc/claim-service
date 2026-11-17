package com.objetosperdidos.claimservice.application.usecase;

import com.objetosperdidos.claimservice.application.dto.ClaimMetrics;
import com.objetosperdidos.claimservice.application.port.in.GetMetricsUseCase;
import com.objetosperdidos.claimservice.domain.model.ClaimStatus;
import com.objetosperdidos.claimservice.domain.port.out.ClaimRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class GetMetricsService implements GetMetricsUseCase {

    private final ClaimRepositoryPort repo;

    public GetMetricsService(ClaimRepositoryPort repo) { this.repo = repo; }

    @Override
    public ClaimMetrics get() {
        long total    = repo.count();
        long pendingO = repo.countByStatus(ClaimStatus.PENDING_OTP);
        long pending  = repo.countByStatus(ClaimStatus.PENDING);
        long approved = repo.countByStatus(ClaimStatus.APPROVED);
        long rejected = repo.countByStatus(ClaimStatus.REJECTED);
        long flagged  = repo.countByStatus(ClaimStatus.FLAGGED);

        long resolved = approved + rejected;
        double approvalRate = resolved == 0 ? 0.0
                : Math.round(approved * 1000.0 / resolved) / 10.0;
        double fraudRate = total == 0 ? 0.0
                : Math.round(flagged * 1000.0 / total) / 10.0;

        return new ClaimMetrics(total, pendingO, pending, approved, rejected, flagged,
                approvalRate, fraudRate);
    }
}