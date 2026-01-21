package com.aseguratupata.policy_service.domain.model;

public enum Species {
    DOG(0.20), CAT(0.10);

    private final double riskFactor;

    Species(double riskFactor) { this.riskFactor = riskFactor; }

    public double getRiskFactor() {
        return riskFactor;
    }
}
