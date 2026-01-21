package com.aseguratupata.policy_service.infrastructure.adapters.web;

import com.aseguratupata.policy_service.application.usecase.CreateQuoteUseCase;
import com.aseguratupata.policy_service.domain.model.Pet;
import com.aseguratupata.policy_service.domain.model.Plan;
import com.aseguratupata.policy_service.domain.model.Species;
import com.aseguratupata.policy_service.infrastructure.adapters.web.dto.QuoteRequest;
import com.aseguratupata.policy_service.infrastructure.adapters.web.dto.QuoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {
    private final CreateQuoteUseCase createQuoteUseCase;

    @PostMapping
    public Mono<QuoteResponse> createQuote(@RequestBody QuoteRequest request) {
        Pet pet = new Pet(
                request.petName(),
                Species.valueOf(request.species().toUpperCase()),
                request.breed(),
                request.age()
        );

        Plan plan = Plan.valueOf(request.plan().toUpperCase());

        return createQuoteUseCase.execute(pet, plan)
                .map(quote -> new QuoteResponse(
                        quote.id(),
                        quote.pet().name(),
                        quote.totalAmount().doubleValue(),
                        quote.expirationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                ));
    }
}
