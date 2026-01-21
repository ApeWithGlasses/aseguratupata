package com.aseguratupata.policy_service.infrastructure.adapters.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record QuoteRequest(
        @NotBlank String petName,
        @NotBlank String species,
        String breed,
        @Min(0) int age,
        @NotBlank String plan
) {
}
