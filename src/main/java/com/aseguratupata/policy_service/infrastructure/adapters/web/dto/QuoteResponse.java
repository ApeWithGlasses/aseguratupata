package com.aseguratupata.policy_service.infrastructure.adapters.web.dto;

public record QuoteResponse(
        String id,
        String petName,
        double totalAmount,
        String expirationDate
) {
}
