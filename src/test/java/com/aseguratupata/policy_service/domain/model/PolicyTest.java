package com.aseguratupata.policy_service.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PolicyTest {

    static Stream<Object[]> policyCases() {
        return Stream.of(
                new Object[]{new Owner("Carlos", "ID123", "carlos@example.com"), Quote.create(new Pet("Firulais", Species.DOG, "Labrador", 3), Plan.BASIC, new BigDecimal("100.00"))},
                new Object[]{new Owner("Ana", "ID456", "ana@domain.co"), Quote.create(new Pet("Misu", Species.CAT, "Siames", 5), Plan.PREMIUM, new BigDecimal("250.50"))}
        );
    }

    @ParameterizedTest
    @MethodSource("policyCases")
    void fromQuoteMapsFields(Owner owner, Quote quote) {
        LocalDateTime before = LocalDateTime.now();
        Policy policy = Policy.fromQuote(quote, owner);

        assertNotNull(policy.id());
        assertEquals(quote.id(), policy.quoteId());
        assertEquals(owner, policy.owner());
        assertEquals(PolicyStatus.ACTIVE, policy.status());
        assertNotNull(policy.issuedAt());
        assertTrue(policy.issuedAt().isAfter(before.minusSeconds(1)));
        assertTrue(policy.issuedAt().isBefore(before.plusSeconds(5)));
    }
}