package com.aseguratupata.policy_service.domain.service;

import com.aseguratupata.policy_service.domain.model.Pet;
import com.aseguratupata.policy_service.domain.model.Plan;
import com.aseguratupata.policy_service.domain.model.Species;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PricingServiceTest {

    private static final Species SPECIES_DOG = Species.DOG;
    private static final Species SPECIES_CAT = Species.CAT;
    private static final String FIRULAIS = "Firulais";
    private static final String MISU = "Misu";
    public static final int AGE_3 = 3;
    public static final int AGE_6 = 6;
    public static final Plan PLAN_BASIC = Plan.BASIC;
    public static final Plan PLAN_PREMIUM = Plan.PREMIUM;
    public static final String BREED_LABRADOR = "Labrador";
    public static final String BREED_SIAMES = "Siames";

    static Stream<Object[]> priceCases() {
        return Stream.of(
            new Object[]{new Pet(FIRULAIS, SPECIES_DOG, BREED_LABRADOR, AGE_3), PLAN_BASIC, new BigDecimal("12.00")},
            new Object[]{new Pet(MISU, SPECIES_CAT, BREED_SIAMES, AGE_3), PLAN_BASIC, new BigDecimal("11.00")},
            new Object[]{new Pet(FIRULAIS, SPECIES_DOG, BREED_LABRADOR, AGE_6), PLAN_BASIC, new BigDecimal("18.00")},
            new Object[]{new Pet(MISU, SPECIES_CAT, BREED_SIAMES, AGE_6), PLAN_BASIC, new BigDecimal("16.50")},
            new Object[]{new Pet(FIRULAIS, SPECIES_DOG, BREED_LABRADOR, AGE_3), PLAN_PREMIUM, new BigDecimal("24.00")},
            new Object[]{new Pet(MISU, SPECIES_CAT, BREED_SIAMES, AGE_3), PLAN_PREMIUM, new BigDecimal("22.00")},
            new Object[]{new Pet(FIRULAIS, SPECIES_DOG, BREED_LABRADOR, AGE_6), PLAN_PREMIUM, new BigDecimal("36.00")},
            new Object[]{new Pet(MISU, SPECIES_CAT, BREED_SIAMES, AGE_6), PLAN_PREMIUM, new BigDecimal("33.00")}
        );
    }

    @ParameterizedTest
    @MethodSource("priceCases")
    void testCalculatePrice(Pet pet, Plan plan, BigDecimal expected) {
        // Arrange
        PricingService service = new PricingService();

        // Act
        BigDecimal result = service.calculatePrice(pet, plan);

        // Assert
        assertEquals(expected, result);
    }
}