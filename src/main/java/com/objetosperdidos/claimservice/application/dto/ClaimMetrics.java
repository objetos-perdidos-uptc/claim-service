package com.objetosperdidos.claimservice.application.dto;

public record ClaimMetrics(long total, long pendingOtp, long pending,
                           long approved, long rejected, long flagged,
                           double approvalRate, double fraudRate) {}