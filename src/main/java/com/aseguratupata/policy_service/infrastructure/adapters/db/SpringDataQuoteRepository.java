package com.aseguratupata.policy_service.infrastructure.adapters.db;

import com.aseguratupata.policy_service.infrastructure.adapters.db.entities.QuoteEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataQuoteRepository extends ReactiveCrudRepository<QuoteEntity, String> {
}
