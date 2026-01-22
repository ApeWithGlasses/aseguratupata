package com.aseguratupata.policy_service.infrastructure.adapters.db;

import com.aseguratupata.policy_service.domain.model.Pet;
import com.aseguratupata.policy_service.domain.model.Plan;
import com.aseguratupata.policy_service.domain.model.Quote;
import com.aseguratupata.policy_service.domain.model.Species;
import com.aseguratupata.policy_service.infrastructure.adapters.db.entities.QuoteEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Import(QuoteRepositoryAdapter.class)
class QuoteRepositoryAdapterIntegrationTest {

    @Autowired
    private QuoteRepositoryAdapter adapter;

    @Autowired
    private R2dbcEntityTemplate template;

    @Test
    void save_shouldPersistDomainObjectAsEntity() {
        Pet pet = new Pet("Bruno", Species.DOG, "Beagle", 4);
        Quote domainQuote = Quote.create(pet, Plan.PREMIUM, new BigDecimal("150.00"));

        StepVerifier.create(adapter.save(domainQuote))
                .expectNextMatches(saved -> saved.id().equals(domainQuote.id()))
                .verifyComplete();

        StepVerifier.create(template.select(QuoteEntity.class)
                        .from("quotes")
                        .matching(org.springframework.data.relational.core.query.Query.query(
                                org.springframework.data.relational.core.query.Criteria.where("id").is(domainQuote.id())))
                        .one())
                .assertNext(entity -> {
                    assertEquals(domainQuote.id(), entity.getId());
                    assertEquals("Bruno", entity.getPetName());
                    assertEquals("DOG", entity.getPetSpecies());
                    assertEquals("Beagle", entity.getPetBreed());
                    assertEquals(4, entity.getPetAge());
                    assertEquals("PREMIUM", entity.getSelectedPlan());
                    assertEquals(0, new BigDecimal("150.00").compareTo(entity.getTotalAmount()));
                    assertNotNull(entity.getExpirationDate());
                    assertFalse(entity.isNew());
                })
                .verifyComplete();
    }

    @Test
    void findById_shouldMapEntityBackToDomain() {
        String randomId = java.util.UUID.randomUUID().toString();

        QuoteEntity entity = QuoteEntity.builder()
                .id(randomId)
                .petName("Luna")
                .petSpecies("CAT")
                .petBreed("Siames")
                .petAge(2)
                .selectedPlan("BASIC")
                .totalAmount(new BigDecimal("80.00"))
                .expirationDate(LocalDateTime.now().plusDays(5))
                .isNewRecord(true)
                .build();

        template.insert(entity).block();

        StepVerifier.create(adapter.findById(randomId))
                .assertNext(quote -> {
                    assertEquals(randomId, quote.id()); // <--- Validar contra el randomId
                    assertEquals("Luna", quote.pet().name());
                    assertEquals(Species.CAT, quote.pet().species());
                    assertEquals("Siames", quote.pet().breed());
                    assertEquals(2, quote.pet().age());
                    assertEquals(Plan.BASIC, quote.plan());
                    assertEquals(0, new BigDecimal("80.00").compareTo(quote.totalAmount()));
                })
                .verifyComplete();
    }
}