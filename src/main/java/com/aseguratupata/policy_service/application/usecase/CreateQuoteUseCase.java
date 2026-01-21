package com.aseguratupata.policy_service.application.usecase;

import com.aseguratupata.policy_service.domain.model.Pet;
import com.aseguratupata.policy_service.domain.model.Plan;
import com.aseguratupata.policy_service.domain.model.Quote;
import com.aseguratupata.policy_service.domain.ports.QuoteRepository;
import com.aseguratupata.policy_service.domain.service.PricingService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CreateQuoteUseCase {
    private final QuoteRepository quoteRepository;
    private final PricingService pricingService;

    public Mono<Quote> execute(Pet pet, Plan plan) {
        return Mono.fromCallable(() -> {
            BigDecimal amount = pricingService.calculatePrice(pet, plan);

            return Quote.create(pet, plan, amount);
        }).flatMap(quoteRepository::save);
    }
}
