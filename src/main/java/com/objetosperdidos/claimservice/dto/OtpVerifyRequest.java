package com.objetosperdidos.claimservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpVerifyRequest(
    @NotBlank @Pattern(regexp = "\\d{6}", message = "El OTP debe tener 6 dígitos") String code
){}
