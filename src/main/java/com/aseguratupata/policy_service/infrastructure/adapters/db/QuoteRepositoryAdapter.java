package com.aseguratupata.policy_service.infrastructure.adapters.db;

import com.aseguratupata.policy_service.domain.model.Pet;
import com.aseguratupata.policy_service.domain.model.Plan;
import com.aseguratupata.policy_service.domain.model.Quote;
import com.aseguratupata.policy_service.domain.model.Species;
import com.aseguratupata.policy_service.domain.ports.QuoteRepository;
import com.aseguratupata.policy_service.infrastructure.adapters.db.entities.QuoteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class QuoteRepositoryAdapter implements QuoteRepository {
    private final SpringDataQuoteRepository repository;

    @Override
    public Mono<Quote> save(Quote quote) {
        QuoteEntity entity = QuoteEntity.builder()
                .id(quote.id())
                .petName(quote.pet().name())
                .petAge(quote.pet().age())
                .petSpecies(quote.pet().species().name())
                .petBreed(quote.pet().breed())
                .selectedPlan(quote.plan().name())
                .totalAmount(quote.totalAmount())
                .expirationDate(quote.expirationDate())
                .isNewRecord(true)
                .build();

        return repository.save(entity)
                .map(e -> quote);
    }

    @Override
    public Mono<Quote> findById(String id) {
        return repository.findById(id)
                .map(e -> new Quote(
                        e.getId(),
                        new Pet(e.getPetName(), Species.valueOf(e.getPetSpecies()), e.getPetBreed(), e.getPetAge()),
                        Plan.valueOf(e.getSelectedPlan()),
                        e.getTotalAmount(),
                        e.getExpirationDate()
                ));
    }
}
