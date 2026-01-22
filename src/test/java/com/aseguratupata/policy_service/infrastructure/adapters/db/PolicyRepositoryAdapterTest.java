package com.aseguratupata.policy_service.infrastructure.adapters.db;

import com.aseguratupata.policy_service.domain.model.Owner;
import com.aseguratupata.policy_service.domain.model.Policy;
import com.aseguratupata.policy_service.domain.model.PolicyStatus;
import com.aseguratupata.policy_service.infrastructure.adapters.db.entities.PolicyEntity;
import com.aseguratupata.policy_service.infrastructure.adapters.db.entities.QuoteEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Import(PolicyRepositoryAdapter.class)
class PolicyRepositoryAdapterIntegrationTest {

    @Autowired
    private PolicyRepositoryAdapter adapter;

    @Autowired
    private R2dbcEntityTemplate template;

    @Test
    void save_shouldPersistPolicyAndMapFieldsCorrectly() {
        String policyId = UUID.randomUUID().toString();
        String quoteId = UUID.randomUUID().toString();

        QuoteEntity parentQuote = QuoteEntity.builder()
                .id(quoteId)
                .petName("Parent Pet")
                .petSpecies("DOG")
                .petBreed("Mix")
                .petAge(5)
                .selectedPlan("BASIC")
                .totalAmount(BigDecimal.TEN)
                .expirationDate(LocalDateTime.now().plusDays(30))
                .isNewRecord(true)
                .build();

        template.insert(parentQuote).block();

        Owner owner = new Owner("Maria Lopez", "987654321", "maria@example.com");
        Policy domainPolicy = new Policy(
                policyId,
                quoteId,
                owner,
                PolicyStatus.ACTIVE,
                LocalDateTime.now()
        );

        StepVerifier.create(adapter.save(domainPolicy))
                .expectNext(domainPolicy)
                .verifyComplete();

        StepVerifier.create(template.select(PolicyEntity.class)
                        .matching(Query.query(Criteria.where("id").is(policyId)))
                        .one())
                .assertNext(entity -> {
                    assertEquals(policyId, entity.getId());
                    assertEquals(quoteId, entity.getQuoteId());
                    assertEquals("Maria Lopez", entity.getOwnerName());
                    assertEquals("ACTIVE", entity.getStatus());
                    assertFalse(entity.isNew());
                })
                .verifyComplete();
    }
}