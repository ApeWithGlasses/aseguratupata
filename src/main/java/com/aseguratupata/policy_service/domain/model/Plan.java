package com.aseguratupata.policy_service.domain.model;

public enum Plan {
    BASIC(1.0), PREMIUM(2.0);

    private final double multiplier;

    Plan(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
