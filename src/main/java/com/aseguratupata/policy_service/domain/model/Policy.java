package com.aseguratupata.policy_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Policy(
        String id,
        String quoteId,
        Owner owner,
        PolicyStatus status,
        LocalDateTime issuedAt
) {
    public static Policy fromQuote(Quote quote, Owner owner) {
        return new Policy(
                UUID.randomUUID().toString(),
                quote.id(),
                owner,
                PolicyStatus.ACTIVE,
                LocalDateTime.now()
        );
    }
}
