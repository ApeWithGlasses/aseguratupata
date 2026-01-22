package com.aseguratupata.policy_service.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class QuoteTest {

    static Stream<Object[]> quoteCases() {
        return Stream.of(
                new Object[]{new Pet("Firulais", Species.DOG, "Labrador", 3), Plan.BASIC, new BigDecimal("100.00")},
                new Object[]{new Pet("Misu", Species.CAT, "Siames", 5), Plan.PREMIUM, new BigDecimal("250.50")},
                new Object[]{new Pet("Buddy", Species.DOG, "Mixed", 0), Plan.BASIC, new BigDecimal("0.01")}
        );
    }

    @ParameterizedTest
    @MethodSource("quoteCases")
    void createQuoteSetsFields(Pet pet, Plan plan, BigDecimal amount) {
        LocalDateTime before = LocalDateTime.now();
        Quote quote = Quote.create(pet, plan, amount);

        assertNotNull(quote.id());
        assertEquals(pet, quote.pet());
        assertEquals(plan, quote.plan());
        assertEquals(amount, quote.totalAmount());
        assertNotNull(quote.expirationDate());
        assertTrue(quote.expirationDate().isAfter(before.plusDays(29)));
        assertTrue(quote.expirationDate().isBefore(before.plusDays(31)));
    }
}