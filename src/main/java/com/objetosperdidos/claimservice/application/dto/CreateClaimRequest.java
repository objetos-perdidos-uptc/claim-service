package com.objetosperdidos.claimservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClaimRequest(
    @NotBlank String reportId,
    @NotBlank @Size(max = 1000) String description,
    @NotBlank String contactPhone){}   
