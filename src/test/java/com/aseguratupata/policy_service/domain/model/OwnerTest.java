package com.aseguratupata.policy_service.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerTest {

    static Stream<Object[]> validOwnerCases() {
        return Stream.of(
                new Object[]{"Carlos", "ID123", "carlos@example.com"},
                new Object[]{"Ana", "ID456", "ana@domain.co"},
                new Object[]{"Luis", "ID789", "luis@mail.org"}
        );
    }

    @ParameterizedTest
    @MethodSource("validOwnerCases")
    void createsOwnerWithValidEmail(String name, String idNumber, String email) {
        Owner owner = new Owner(name, idNumber, email);
        assertEquals(name, owner.name());
        assertEquals(idNumber, owner.idNumber());
        assertEquals(email, owner.email());
    }

    static Stream<Object[]> invalidOwnerCases() {
        return Stream.of(
                new Object[]{"NoEmail", "ID000", null},
                new Object[]{"BadFormat", "ID001", "noatsign"},
                new Object[]{"Empty", "ID002", ""}
        );
    }

    @ParameterizedTest
    @MethodSource("invalidOwnerCases")
    void rejectsInvalidEmail(String name, String idNumber, String email) {
        assertThrows(IllegalArgumentException.class, () -> new Owner(name, idNumber, email));
    }
}