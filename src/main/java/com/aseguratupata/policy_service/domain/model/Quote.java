package com.aseguratupata.policy_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Quote(
        String id,
        Pet pet,
        Plan plan,
        BigDecimal totalAmount,
        LocalDateTime expirationDate
) {
    public static Quote create(Pet pet, Plan plan, BigDecimal amount) {
        return new Quote(
                UUID.randomUUID().toString(),
                pet,
                plan,
                amount,
                LocalDateTime.now().plusDays(30)
        );
    }
}
