package com.aseguratupata.policy_service.domain.model;

public record Owner(
        String name,
        String idNumber,
        String email
) {
    public Owner {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }
}
