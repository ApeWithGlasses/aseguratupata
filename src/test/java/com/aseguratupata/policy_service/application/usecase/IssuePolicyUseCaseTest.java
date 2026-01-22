package com.aseguratupata.policy_service.application.usecase;

import com.aseguratupata.policy_service.domain.model.Owner;
import com.aseguratupata.policy_service.domain.model.Policy;
import com.aseguratupata.policy_service.domain.model.Pet;
import com.aseguratupata.policy_service.domain.model.Plan;
import com.aseguratupata.policy_service.domain.model.Quote;
import com.aseguratupata.policy_service.domain.model.Species;
import com.aseguratupata.policy_service.domain.model.events.PolicyIssuedEvent;
import com.aseguratupata.policy_service.domain.ports.EventPublisher;
import com.aseguratupata.policy_service.domain.ports.PolicyRepository;
import com.aseguratupata.policy_service.domain.ports.QuoteRepository;
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
import java.util.stream.Stream;

class IssuePolicyUseCaseTest {

    static Stream<BigDecimal> amounts() {
        return Stream.of(new BigDecimal("10.00"), new BigDecimal("100.50"), new BigDecimal("0.01"));
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void executeCreatesPolicyAndPublishesEvent(BigDecimal amount) {
        QuoteRepository quoteRepository = Mockito.mock(QuoteRepository.class);
        PolicyRepository policyRepository = Mockito.mock(PolicyRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);

        Pet pet = new Pet("Buddy", Species.DOG, "Mixed", 3);
        Quote quote = Quote.create(pet, Plan.BASIC, amount);

        Owner owner = new Owner("Carlos", "ID123", "carlos@example.com");

        IssuePolicyUseCase useCase = new IssuePolicyUseCase(quoteRepository, policyRepository, eventPublisher);

        BDDMockito.given(quoteRepository.findById(ArgumentMatchers.eq(quote.id())))
                .willReturn(Mono.just(quote));

        ArgumentCaptor<Policy> savedCaptor = ArgumentCaptor.forClass(Policy.class);
        BDDMockito.given(policyRepository.save(savedCaptor.capture()))
                .willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        BDDMockito.given(eventPublisher.publish(ArgumentMatchers.any(PolicyIssuedEvent.class)))
                .willReturn(Mono.empty());

        StepVerifier.create(useCase.execute(quote.id(), owner))
                .assertNext(policy -> {
                    assert policy.id() != null;
                    assert policy.quoteId().equals(quote.id());
                    assert policy.owner().equals(owner);
                    assert policy.status().name().equals("ACTIVE");
                    assert policy.issuedAt() != null;
                })
                .verifyComplete();

        ArgumentCaptor<PolicyIssuedEvent> eventCaptor = ArgumentCaptor.forClass(PolicyIssuedEvent.class);
        Mockito.verify(eventPublisher).publish(eventCaptor.capture());
        PolicyIssuedEvent capturedEvent = eventCaptor.getValue();
        assert capturedEvent.policyId().equals(savedCaptor.getValue().id());
        assert capturedEvent.ownerEmail().equals(owner.email());
        assert capturedEvent.amount().equals(amount);
        assert capturedEvent.issuedAt() != null;
    }

    @Test
    void executeReturnsErrorWhenQuoteNotFound() {
        QuoteRepository quoteRepository = Mockito.mock(QuoteRepository.class);
        PolicyRepository policyRepository = Mockito.mock(PolicyRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);

        IssuePolicyUseCase useCase = new IssuePolicyUseCase(quoteRepository, policyRepository, eventPublisher);

        BDDMockito.given(quoteRepository.findById(ArgumentMatchers.eq("unknown")))
                .willReturn(Mono.empty());

        StepVerifier.create(useCase.execute("unknown", new Owner("X","Y","z@a.com")))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException && throwable.getMessage().contains("La cotización no existe"))
                .verify();
    }

    @Test
    void executeReturnsErrorWhenQuoteExpired() {
        QuoteRepository quoteRepository = Mockito.mock(QuoteRepository.class);
        PolicyRepository policyRepository = Mockito.mock(PolicyRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);

        Pet pet = new Pet("Oldie", Species.CAT, "Breed", 1);
        Quote expired = new Quote("q-1", pet, Plan.BASIC, new BigDecimal("10.00"), LocalDateTime.now().minusDays(1));

        IssuePolicyUseCase useCase = new IssuePolicyUseCase(quoteRepository, policyRepository, eventPublisher);

        BDDMockito.given(quoteRepository.findById(ArgumentMatchers.eq(expired.id())))
                .willReturn(Mono.just(expired));

        StepVerifier.create(useCase.execute(expired.id(), new Owner("O","ID","o@d.com")))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException && throwable.getMessage().contains("La cotización ha expirado"))
                .verify();
    }

    @Test
    void executePropagatesPublisherError() {
        QuoteRepository quoteRepository = Mockito.mock(QuoteRepository.class);
        PolicyRepository policyRepository = Mockito.mock(PolicyRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);

        Pet pet = new Pet("Buddy", Species.DOG, "Mixed", 3);
        Quote quote = Quote.create(pet, Plan.BASIC, new BigDecimal("50.00"));
        Owner owner = new Owner("Carlos", "ID123", "carlos@example.com");

        IssuePolicyUseCase useCase = new IssuePolicyUseCase(quoteRepository, policyRepository, eventPublisher);

        BDDMockito.given(quoteRepository.findById(ArgumentMatchers.eq(quote.id())))
                .willReturn(Mono.just(quote));

        BDDMockito.given(policyRepository.save(ArgumentMatchers.any()))
                .willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        BDDMockito.given(eventPublisher.publish(ArgumentMatchers.any()))
                .willReturn(Mono.error(new RuntimeException("publisher failed")));

        StepVerifier.create(useCase.execute(quote.id(), owner))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("publisher failed"))
                .verify();
    }
}