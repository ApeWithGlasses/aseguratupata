package com.aseguratupata.policy_service.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PetTest {

    private static final Species SPECIES_DOG = Species.DOG;
    private static final String NAME = "Buddy";
    private static final String BREED_MIXED = "Mixed";

    static Stream<Object[]> validPetCases() {
        return Arrays.stream(Species.values())
                .flatMap(s -> Stream.of(
                        new Object[]{NAME, s, BREED_MIXED, 0},
                        new Object[]{NAME, s, BREED_MIXED, 5},
                        new Object[]{NAME, s, BREED_MIXED, 10}
                ));
    }

    @ParameterizedTest
    @MethodSource("validPetCases")
    void createsPetWithValidAge(String name, Species species, String breed, int age) {
        Pet pet = new Pet(name, species, breed, age);
        assertEquals(name, pet.name());
        assertEquals(species, pet.species());
        assertEquals(breed, pet.breed());
        assertEquals(age, pet.age());
    }

    static Stream<Object[]> invalidAgeCases() {
        return Stream.of(
                new Object[]{11},
                new Object[]{12},
                new Object[]{100}
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAgeCases")
    void rejectsAgeGreaterThanTen(int age) {
        assertThrows(IllegalArgumentException.class, () -> new Pet("Oldie", SPECIES_DOG, "Breed", age));
    }
}