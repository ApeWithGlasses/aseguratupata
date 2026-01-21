package com.aseguratupata.policy_service.domain.ports;

import com.aseguratupata.policy_service.domain.model.Quote;
import reactor.core.publisher.Mono;

public interface QuoteRepository {
    Mono<Quote> save(Quote quote);
    Mono<Quote> findById(String id);
}
