package com.objetosperdidos.claimservice.application.dto;

public record CreateClaimCommand(String reportId, String description, String contactPhone) {}