package com.aseguratupata.policy_service.domain.service;

import com.aseguratupata.policy_service.domain.model.Pet;
import com.aseguratupata.policy_service.domain.model.Plan;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PricingService {
    private static final BigDecimal BASE_PRICE = BigDecimal.valueOf(10.0);

    public BigDecimal calculatePrice(Pet pet, Plan plan) {
        BigDecimal price = BASE_PRICE;

        BigDecimal speciesIncrement = price.multiply(BigDecimal.valueOf(pet.species().getRiskFactor()));
        price = price.add(speciesIncrement);

        if (pet.age() > 5) {
            BigDecimal ageIncrement = price.multiply(BigDecimal.valueOf(0.50));
            price = price.add(ageIncrement);
        }

        price = price.multiply(BigDecimal.valueOf(plan.getMultiplier()));

        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
