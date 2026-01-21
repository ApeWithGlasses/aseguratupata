package com.aseguratupata.policy_service.infrastructure.adapters.web.dto;

public record IssuePolicyResponse(
        String policyId,
        String status,
        String issuedAt,
        String message
) {
}
