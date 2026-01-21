package com.aseguratupata.policy_service.domain.model.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PolicyIssuedEvent(
        String policyId,
        String ownerEmail,
        BigDecimal amount,
        LocalDateTime issuedAt
) {
}
