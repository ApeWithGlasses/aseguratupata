package com.aseguratupata.policy_service.domain.model;

public record Pet(
        String name,
        Species species,
        String breed,
        int age
) {
    public Pet {
        if (age > 10) {
            throw new IllegalArgumentException("No aseguramos a mascotas mayores a 10 años.");
        }
    }
}
