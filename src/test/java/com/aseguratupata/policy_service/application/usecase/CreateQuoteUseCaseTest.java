package com.aseguratupata.policy_service.application.usecase;

import com.aseguratupata.policy_service.domain.model.Pet;
import com.aseguratupata.policy_service.domain.model.Plan;
import com.aseguratupata.policy_service.domain.model.Quote;
import com.aseguratupata.policy_service.domain.model.Species;
import com.aseguratupata.policy_service.domain.ports.QuoteRepository;
import com.aseguratupata.policy_service.domain.service.PricingService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CreateQuoteUseCaseTest {

    static Stream<Object[]> validCases() {
        return Stream.of(
                new Object[]{new Pet("Buddy", Species.DOG, "Mixed", 1), Plan.BASIC, new BigDecimal("50.00")},
                new Object[]{new Pet("Misu", Species.CAT, "Siamese", 5), Plan.PREMIUM, new BigDecimal("120.75")},
                new Object[]{new Pet("Rex", Species.DOG, "Bulldog", 0), Plan.BASIC, new BigDecimal("0.01")}
        );
    }

    @ParameterizedTest
    @MethodSource("validCases")
    void executeReturnsQuoteAndSaves(Pet pet, Plan plan, BigDecimal amount) {
        QuoteRepository quoteRepository = Mockito.mock(QuoteRepository.class);
        PricingService pricingService = Mockito.mock(PricingService.class);

        CreateQuoteUseCase useCase = new CreateQuoteUseCase(quoteRepository, pricingService);

        BDDMockito.given(pricingService.calculatePrice(ArgumentMatchers.eq(pet), ArgumentMatchers.eq(plan)))
                .willReturn(amount);

        ArgumentCaptor<Quote> captor = ArgumentCaptor.forClass(Quote.class);
        BDDMockito.given(quoteRepository.save(captor.capture()))
                .willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.execute(pet, plan))
                .assertNext(q -> {
                    assertNotNull(q.id());
                    assertEquals(pet, q.pet());
                    assertEquals(plan, q.plan());
                    assertEquals(amount, q.totalAmount());
                    assertNotNull(q.expirationDate());
                    long days = ChronoUnit.DAYS.between(LocalDateTime.now(), q.expirationDate());
                    assertTrue(days >= 29 && days <= 31);
                })
                .verifyComplete();

        Mockito.verify(pricingService).calculatePrice(pet, plan);
        Mockito.verify(quoteRepository).save(ArgumentMatchers.any(Quote.class));

        Quote saved = captor.getValue();
        assertEquals(pet, saved.pet());
        assertEquals(plan, saved.plan());
        assertEquals(amount, saved.totalAmount());
    }

    @Test
    void executePropagatesPricingError() {
        QuoteRepository quoteRepository = Mockito.mock(QuoteRepository.class);
        PricingService pricingService = Mockito.mock(PricingService.class);

        CreateQuoteUseCase useCase = new CreateQuoteUseCase(quoteRepository, pricingService);

        Pet pet = new Pet("Buddy", Species.DOG, "Mixed", 2);
        Plan plan = Plan.BASIC;

        BDDMockito.given(pricingService.calculatePrice(ArgumentMatchers.eq(pet), ArgumentMatchers.eq(plan)))
                .willThrow(new RuntimeException("pricing failed"));

        StepVerifier.create(useCase.execute(pet, plan))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("pricing failed"))
                .verify();

        Mockito.verify(pricingService).calculatePrice(pet, plan);
        Mockito.verifyNoInteractions(quoteRepository);
    }

    @Test
    void executePropagatesSaveError() {
        QuoteRepository quoteRepository = Mockito.mock(QuoteRepository.class);
        PricingService pricingService = Mockito.mock(PricingService.class);

        CreateQuoteUseCase useCase = new CreateQuoteUseCase(quoteRepository, pricingService);

        Pet pet = new Pet("Milo", Species.CAT, "Mixed", 4);
        Plan plan = Plan.PREMIUM;
        BigDecimal amount = new BigDecimal("77.77");

        BDDMockito.given(pricingService.calculatePrice(ArgumentMatchers.eq(pet), ArgumentMatchers.eq(plan)))
                .willReturn(amount);

        BDDMockito.given(quoteRepository.save(ArgumentMatchers.any()))
                .willReturn(Mono.error(new RuntimeException("save failed")));

        StepVerifier.create(useCase.execute(pet, plan))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("save failed"))
                .verify();

        Mockito.verify(pricingService).calculatePrice(pet, plan);
        Mockito.verify(quoteRepository).save(ArgumentMatchers.any());
    }
}