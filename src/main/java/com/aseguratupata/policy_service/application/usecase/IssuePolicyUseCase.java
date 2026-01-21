package com.aseguratupata.policy_service.application.usecase;

import com.aseguratupata.policy_service.domain.model.Owner;
import com.aseguratupata.policy_service.domain.model.Policy;
import com.aseguratupata.policy_service.domain.model.events.PolicyIssuedEvent;
import com.aseguratupata.policy_service.domain.ports.EventPublisher;
import com.aseguratupata.policy_service.domain.ports.PolicyRepository;
import com.aseguratupata.policy_service.domain.ports.QuoteRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class IssuePolicyUseCase {
    private final QuoteRepository quoteRepository;
    private final PolicyRepository policyRepository;
    private final EventPublisher eventPublisher;

    public Mono<Policy> execute(String quoteId, Owner owner) {
        return quoteRepository.findById(quoteId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La cotización no existe")))
                .filter(quote -> quote.expirationDate().isAfter(LocalDateTime.now()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La cotización ha expirado")))
                .flatMap(quote -> {
                    Policy policy = Policy.fromQuote(quote, owner);

                    return policyRepository.save(policy)
                            .flatMap(savedPolicy -> {
                                PolicyIssuedEvent event = new PolicyIssuedEvent(
                                        savedPolicy.id(),
                                        owner.email(),
                                        quote.totalAmount(),
                                        savedPolicy.issuedAt()
                                );
                                return eventPublisher.publish(event)
                                        .thenReturn(savedPolicy);
                            });
                });
    }
}
