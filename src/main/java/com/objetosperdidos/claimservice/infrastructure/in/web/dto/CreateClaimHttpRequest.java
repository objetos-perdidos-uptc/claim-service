package com.objetosperdidos.claimservice.infrastructure.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClaimHttpRequest(
        @NotBlank String reportId,
        @NotBlank @Size(max = 1000) String description,
        @NotBlank String contactPhone) {}