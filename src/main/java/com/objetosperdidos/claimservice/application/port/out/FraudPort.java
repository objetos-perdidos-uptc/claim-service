package com.objetosperdidos.claimservice.application.port.out;

import com.objetosperdidos.claimservice.domain.model.FraudAssessment;

public interface FraudPort {
    FraudAssessment evaluate(String claimantId, String reportId,
                             String description, String contactPhone);
}